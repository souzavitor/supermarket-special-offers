(ns supermarket-special-offers.logic.special_offers
  (:require
   [clojure.string :as str]
   [clojure.java.io :as io])
  (:gen-class))

;; Define offers state
(def offers (atom {}))

(defn parse-special-offer-rule [special-offer-rule]
  (if (= special-offer-rule "")
    {:special-cond 0 :special-price 0}
    (let [rules (str/split special-offer-rule #" for ")]
      {:special-cond (Integer/parseInt (get rules 0))
       :special-price (Float/parseFloat (get rules 1))})))

;; Transform a offer-vector to a offer-map
;; The hash map has these attributes :sku :price :offer
(defn offer-vector->offer-map [offer-vector]
  (merge
   {:sku (get offer-vector 0)
    :price (Float/parseFloat (get offer-vector 1))}
   (parse-special-offer-rule (get offer-vector 2 ""))))

;; Creates the offer-hashmap with a giver vector of 3 positions
(defn create-offer-hashmap [offer-vector]
  {(get offer-vector 0) (offer-vector->offer-map offer-vector)})

;; Merge the offer map with the new offer
(defn with-new-offer [offer-map new-offer]
  (merge-with into offer-map new-offer))

;; Create the list of offers
;; Receives a map of offers and a string with new offer
;; It returns the offer-map updated
(defn create-offer-list [offer-map new-offer-string]
  (->> (create-offer-hashmap (str/split new-offer-string #";"))
       (with-new-offer offer-map)))

;; Add a new offer the offers state using swap!
(defn add-new-offer [new-offer]
  (swap! offers with-new-offer {(get new-offer :sku) new-offer}))

;; Create a HashMap of offers according to file
;; Receives file-path and returns HashMap of offers
(defn create-offers-from-file [file-path]
  (with-open [rdr (io/reader file-path)]
    (reset! offers (reduce create-offer-list {} (line-seq rdr)))))
