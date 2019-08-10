(ns supermarket-special-offers.logic.shopping_cart_test
  (:require [clojure.test :refer :all]
            [clojure.pprint :as pp]
            [supermarket-special-offers.logic.special_offers :as special_offers_logic]
            [supermarket-special-offers.logic.shopping_cart :as shopping_cart_logic]
            [supermarket-special-offers.controllers.shopping_cart :as shopping_cart_controller]))

;; Testing functions to calculate shopping cart total price
(deftest test-shopping-cart-logic
  (testing "Creating shopping cart with new item"
    (special_offers_logic/create-offers-from-file "offers.txt")
    (let [new-item {:sku "A" :qty 10}
          cart {:items {} :total 0.0}
          shopping-cart (shopping_cart_logic/with-new-item cart new-item)]
      (is (map? shopping-cart))
      (is (= (count shopping-cart) 2))
      (is (= (count (:items shopping-cart)) 1))
      (is (map? (:items shopping-cart)))
      (is (contains? (get-in shopping-cart [:items "A"]) :sku))
      (is (contains? (get-in shopping-cart [:items "A"]) :qty))
      (is (contains? (get-in shopping-cart [:items "A"]) :subtotal))
      (is (not= (:total shopping-cart) 0.0))))
  (testing "Calculate total price of an item without special offers"
    (let [new-offer {:sku "A" :special-cond 0 :special-price 0 :price 7.0}
          offer-list (special_offers_logic/add-new-offer new-offer)
          item {:sku "A" :qty 10}
          price (shopping_cart_logic/calc-shopping-cart-item-price item)]
      (is (= price 70.0))))
  (testing "Calculate total price of an item with special offers"
    (let [new-offer {:sku "A" :special-cond 2 :special-price 10.0 :price 7.0}
          offer-list (special_offers_logic/add-new-offer new-offer)
          item {:sku "A" :qty 10}
          price (shopping_cart_logic/calc-shopping-cart-item-price item)]
      (is (= price 50.0))))
  (testing "Calculating the total price of the shopping cart"
    (special_offers_logic/create-offers-from-file "offers.txt")
    (shopping_cart_controller/add-item {:sku "A" :qty 10})
    (shopping_cart_controller/add-item {:sku "B" :qty 5})
    (shopping_cart_controller/add-item {:sku "C" :qty 2})
    (let [cart @shopping_cart_controller/shopping-cart
          total-price (shopping_cart_logic/calc-shopping-cart-price cart)]
      (is (= total-price 115.00)))))