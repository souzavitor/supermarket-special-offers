(ns supermarket-special-offers.controllers.shopping_cart_test
  (:require
    [clojure.test :refer :all]
    [clojure.pprint :as pp]
    [supermarket-special-offers.logic.special_offers :as special_offers_logic]
    [supermarket-special-offers.controllers.shopping_cart :as shopping_cart_controller]))

(deftest test-shopping-cart-controller
  (testing "Adding nonexistent item to the shopping cart"
    (is
      (thrown-with-msg?
        IllegalArgumentException
        #"Offer \"DONTDOIT\" does not exist"
        (shopping_cart_controller/add-item {:sku "DONTDOIT" :qty 100}))))
  (testing "Adding nonexistent item to the shopping cart"
    (is
      (thrown-with-msg?
        IllegalArgumentException
        #"Offer \"DONTDOIT\" does not exist"
        (shopping_cart_controller/scan-item "DONTDOIT"))))
  (testing "Add new items in the shopping cart state"
    (special_offers_logic/create-offers-from-file "offers.txt")
    (shopping_cart_controller/scan-item "A")
    (is (= (get-in @shopping_cart_controller/shopping-cart [:items "A" :qty]) 1))
    (shopping_cart_controller/scan-item "A")
    (shopping_cart_controller/scan-item "A")
    (shopping_cart_controller/scan-item "A")
    (is (= (get-in @shopping_cart_controller/shopping-cart [:items "A" :qty]) 4))
    (is (= (count @shopping_cart_controller/shopping-cart) 2))
    (is (= (count (get @shopping_cart_controller/shopping-cart :items)) 1))
    (is (contains? (get @shopping_cart_controller/shopping-cart :items) "A"))
    (is (contains? (get-in @shopping_cart_controller/shopping-cart [:items "A"]) :sku))
    (is (contains? (get-in @shopping_cart_controller/shopping-cart [:items "A"]) :qty)))
  (testing "Add new items in the shopping cart state"
    (special_offers_logic/create-offers-from-file "offers.txt")
    (shopping_cart_controller/add-item {:sku "A" :qty 10})
    (is (= (count @shopping_cart_controller/shopping-cart) 2))
    (is (= (count (get @shopping_cart_controller/shopping-cart :items)) 1))
    (is (contains? (get @shopping_cart_controller/shopping-cart :items) "A"))
    (is (contains? (get-in @shopping_cart_controller/shopping-cart [:items "A"]) :sku))
    (is (contains? (get-in @shopping_cart_controller/shopping-cart [:items "A"]) :qty)))
  (testing "Calculate price as you add items"
    (special_offers_logic/create-offers-from-file "offers.txt")
    (shopping_cart_controller/empty-shopping-cart)
    (shopping_cart_controller/add-item {:sku "A" :qty 10})
    (shopping_cart_controller/add-item {:sku "B" :qty 5})
    (shopping_cart_controller/add-item {:sku "C" :qty 2})
    (is (= (get @shopping_cart_controller/shopping-cart :total) 115.0)))
  (testing "Empty shopping cart"
    (special_offers_logic/create-offers-from-file "offers.txt")
    (shopping_cart_controller/add-item {:sku "A" :qty 10})
    (shopping_cart_controller/add-item {:sku "B" :qty 5})
    (shopping_cart_controller/add-item {:sku "C" :qty 2})
    (shopping_cart_controller/empty-shopping-cart)
    (is (= (get @shopping_cart_controller/shopping-cart :total) 0.0))))
