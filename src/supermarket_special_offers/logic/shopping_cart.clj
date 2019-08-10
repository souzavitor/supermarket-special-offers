(ns supermarket-special-offers.logic.shopping_cart
  (:require [supermarket-special-offers.logic.special_offers :as special_offers_logic])
  (:gen-class))

;; Calculate a offer by multiplying the price by the quantity
(defn calc-offer [price qty]
  (* price qty))

;; Calculate a special offer by separating the groups according to special conditions
(defn calc-special-offer
  [special-price special-cond price qty]
  (let [unit-qty (rem qty special-cond)
        special-group-qty (quot qty special-cond)]
    (+ (* price unit-qty) (* special-price special-group-qty))))

;; Calculate the price of item according to special offer rules or regular offer rules
(defn calc-shopping-cart-item-price [item-vector]
  (let [item (get item-vector 1)
        qty (get item :qty)
        offer-rules (get @special_offers_logic/offers (get item :sku))
        special-price (get offer-rules :special-price)
        special-cond (get offer-rules :special-cond)
        price (get offer-rules :price)]
    (if (= (get offer-rules :special-cond) 0)
      (calc-offer price qty)
      (calc-special-offer special-price special-cond price qty))))

;; Calculate chopping cart total price
(defn calc-shopping-cart-price [shopping-cart-list]
  (reduce
   (fn [total item] (+ total (calc-shopping-cart-item-price item)))
   0
   (get shopping-cart-list :items)))

;; Add new item or increment quantity
(defn add-or-increment-item [old-item new-item]
  (if (= (get old-item :sku) (get new-item :sku))
    (update old-item :qty + (get new-item :qty))
    (into old-item new-item)))

;; remove item from the shopping-cart
(defn shopping-cart-without-item [shopping-cart-list sku]
  (-> shopping-cart-list
      (update :items dissoc sku)
      ((fn [cart] (assoc cart :total (calc-shopping-cart-price cart))))))

;; Create new shopping cart with the new item
(defn shopping-cart-with-new-item [shopping-cart-list new-item]
  (-> shopping-cart-list
      (update
       :items
       (fn [list item] (merge-with add-or-increment-item list item))
       new-item)
      ((fn [cart] (assoc cart :total (calc-shopping-cart-price cart))))))