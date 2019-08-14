(ns supermarket-special-offers.controllers.special_offers_test
  (:require [clojure.test :refer :all]
            [supermarket-special-offers.controllers.special_offers :as special_offers_controller]
            [supermarket-special-offers.logic.special_offers :as special_offers_logic]))

(deftest test-special-offers-controller
  (testing "Reset Offers state from file"
    (special_offers_controller/new-offers-from-file "offers.txt")
    (is (map @special_offers_controller/offers))
    (is (= (count @special_offers_controller/offers) 3)))
  ;; Testing function that saves a new offer
  (testing "Saves new offer"
    (let [old-offer-list (special_offers_logic/create-offers-from-file "offers.txt")
          new-offer {:sku "Z" :special-cond 2 :special-price 10.0 :price 7.0}
          offer-list (special_offers_controller/add-new-offer new-offer)]
      (is (not= old-offer-list offer-list))
      (is (map? offer-list))
      (is (contains? offer-list "Z"))
      (is (contains? (get offer-list "Z") :sku))
      (is (contains? (get offer-list "Z") :special-cond))
      (is (contains? (get offer-list "Z") :special-price))
      (is (contains? (get offer-list "Z") :price))
      (is (float? (get-in offer-list ["Z" :special-price])))
      (is (integer? (get-in offer-list ["Z" :special-cond])))
      (is (float? (get-in offer-list ["Z" :price]))))))