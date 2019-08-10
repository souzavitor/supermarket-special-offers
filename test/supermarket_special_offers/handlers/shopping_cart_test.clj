(ns supermarket-special-offers.handlers.shopping_cart_test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer [response-for]]
            [io.pedestal.http :as http]
            [clojure.data.json :as json]
            [supermarket-special-offers.logic.special_offers :as special_offers_logic]
            [supermarket-special-offers.controllers.shopping_cart :as shopping_cart_controller]
            [supermarket-special-offers.service :as service]))

(def service
  (::http/service-fn (http/create-servlet service/service)))

(deftest shopping-cart-test
  (special_offers_logic/create-offers-from-file "offers.txt")
  (testing "Get empty shopping cart"
    (is (= (:body (response-for service :get "/api/v1/shopping-cart"))
           (json/write-str {:items {} :total 0.0}))))
  (testing "Get full shopping cart"
    (shopping_cart_controller/scan-item "A")
    (is (= (:body (response-for service :get "/api/v1/shopping-cart"))
           (json/write-str {:items {"A" {:sku "A" :qty 1 :subtotal 10.0}}
                            :total 10.0}))))
  (testing "Delete shopping cart"
    (shopping_cart_controller/scan-item "A")
    (shopping_cart_controller/scan-item "A")
    (is (= (:body (response-for service :delete "/api/v1/shopping-cart")) "")))
  (testing "Post new item"
    (shopping_cart_controller/empty-shopping-cart)
    (is (= (:body (response-for service
                                :post "/api/v1/shopping-cart/item"
                                :headers {"Content-Type" "application/json"}
                                :body (json/write-str {:sku "A" :qty 1})))
           (json/write-str {:items {"A" {:sku "A" :qty 1 :subtotal 10.0}}
                            :total 10.0}))))
  (testing "Delete item"
    (shopping_cart_controller/scan-item "A")
    (shopping_cart_controller/scan-item "A")
    (is (= (:body (response-for service
                                :delete "/api/v1/shopping-cart/item/A"
                                :headers {"Content-Type" "application/json"}))
           (json/write-str {:items {} :total 0.0})))))
