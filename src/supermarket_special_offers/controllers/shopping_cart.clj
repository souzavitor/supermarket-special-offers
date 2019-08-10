(ns supermarket-special-offers.controllers.shopping_cart
  (:require
   [supermarket-special-offers.logic.shopping_cart :as shopping_cart_logic]
   [supermarket-special-offers.logic.special_offers :as special_offers_logic])
  (:gen-class))

;; Initiate shopping cart state
(def shopping-cart
  (atom {:items {}
         :total 0.0}))

;; Remove an item according to sku
(defn remove-item [sku]
  (if (not (contains? (get @shopping-cart :items) sku))
    (throw (IllegalArgumentException. (str "Item \"" sku "\" is not in your shopping cart"))))
  (swap!
   shopping-cart
   shopping_cart_logic/shopping-cart-without-item
   sku))

;; Add a new item in the shopping cart state
(defn add-item [new-item]
  (if (not (contains? @special_offers_logic/offers (get new-item :sku)))
    (throw (IllegalArgumentException. (str "Offer \"" (get new-item :sku) "\" does not exist"))))
  (swap!
   shopping-cart
   shopping_cart_logic/shopping-cart-with-new-item
   {(get new-item :sku) new-item}))

;; Scan one item at a time
(defn scan-item [sku]
  (add-item {:sku sku :qty 1}))

;; Reset shopping cart state
(defn empty-shopping-cart []
  (reset! shopping-cart {:items {} :total 0.0}))
