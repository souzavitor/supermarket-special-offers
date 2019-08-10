(ns supermarket-special-offers.logic.special_offers_test
  (:require
    [clojure.test :refer :all]
    [supermarket-special-offers.logic.special_offers :as special_offers_logic]))

;; Testing the functions of special offers logic
(deftest test-special-offers-logic
  ;; Testing function that parses a vector to a hashmap offer
  (testing "Create offer hashmap with a special offer based on array of values"
    (let [offer-hashmap (special_offers_logic/create-offer-hashmap ["A" "50" "2 for 80"])]
      (is (map? offer-hashmap))
      (is (contains? offer-hashmap "A"))
      (is (contains? (get offer-hashmap "A") :sku))
      (is (contains? (get offer-hashmap "A") :special-cond))
      (is (contains? (get offer-hashmap "A") :special-price))
      (is (contains? (get offer-hashmap "A") :price))
      (is (float? (get-in offer-hashmap ["A" :special-price])))
      (is (integer? (get-in offer-hashmap ["A" :special-cond])))
      (is (float? (get-in offer-hashmap ["A" :price])))))
  ;; Testing function that parses a vector to a hashmap offer without special conditions
  (testing "Create offer hashmap with a special offer based on array of values"
    (let [offer-hashmap (special_offers_logic/create-offer-hashmap ["A" "50"])]
      (is (map? offer-hashmap))
      (is (contains? offer-hashmap "A"))
      (is (contains? (get offer-hashmap "A") :sku))
      (is (contains? (get offer-hashmap "A") :price))
      (is (float? (get-in offer-hashmap ["A" :price])))))
  ;; Testing function that parses a offer string to
  (testing "Return new list of offers with new offer"
    (let [new-offer-hashmap {"A" {:sku "A" :special-cond 2 :special-price 10.0 :price 20.50}}
          old-offer-hashmap {"B" {:sku "B" :special-cond 0 :special-price 0 :price 20.50}}
          offer-list (special_offers_logic/with-new-offer old-offer-hashmap new-offer-hashmap)]
      (is (map? offer-list))
      (is (contains? offer-list "A"))
      (is (contains? offer-list "B"))
      (is (= (count offer-list) 2))))
  ;; Testing if the system is able to load the offer rules from a file properly
  (testing "Load offer rules from file"
    (let [offers (special_offers_logic/create-offers-from-file "offers.txt")] 
      (is (= (count offers) 3))
      (is (map? offers)))))
