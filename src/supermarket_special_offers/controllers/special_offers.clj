(ns supermarket-special-offers.controllers.special_offers
  (:require [supermarket-special-offers.logic.special_offers :as special_offers_logic])
  (:gen-class))

;; Define offers state
(def offers (atom {}))

;; Add a new offer the offers state using swap!
(defn add-new-offer [new-offer]
  (swap! offers special_offers_logic/with-new-offer {(get new-offer :sku) new-offer}))
