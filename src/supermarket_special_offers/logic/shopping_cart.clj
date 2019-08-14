(ns supermarket-special-offers.logic.shopping_cart
  (:require [clojure.pprint :as pp]
            [supermarket-special-offers.controllers.special_offers :as special_offers_controller])
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
(defn calc-shopping-cart-item-price [item]
  (let [qty (get item :qty)
        offer-rules (get @special_offers_controller/offers (get item :sku))
        special-price (get offer-rules :special-price)
        special-cond (get offer-rules :special-cond)
        price (get offer-rules :price)]
    (if (= (get offer-rules :special-cond) 0)
      (calc-offer price qty)
      (calc-special-offer special-price special-cond price qty))))

;; Calculate chopping cart total price
(defn calc-shopping-cart-price [shopping-cart-list]
  (reduce
   #(+ %1 (get-in %2 [1 :subtotal] 0.0))
   0.0
   (get shopping-cart-list :items)))

;; Add new item or increment quantity
(defn add-or-increment-item [old-item new-item]
  (if (= (get old-item :sku) (get new-item :sku))
    (update old-item :qty + (get new-item :qty))
    (into old-item new-item)))

;; Create the item map with the subtotal of each item
(defn- create-item-map [item]
  {(:sku item) (merge item {:subtotal (calc-shopping-cart-item-price item)})})

;; remove item from the shopping-cart
(defn without-item [shopping-cart-list sku]
  (-> shopping-cart-list
      (update :items dissoc sku)
      ((fn [cart] (assoc cart :total (calc-shopping-cart-price cart))))))

;; Create new shopping cart with the new item
(defn with-new-item [shopping-cart-list new-item]
  (-> shopping-cart-list
      (update :items
              #(merge-with add-or-increment-item %1 %2)
              (create-item-map new-item))
      ((fn [cart] (assoc cart :total (calc-shopping-cart-price cart))))))

;; If the new item is decresing the quantity and the new quantity is equal 0
;; we should remove the item
(defn with-or-without-item [shopping-cart-list new-item]
  (if (<= (+ (get-in shopping-cart-list [:items "A" :qty] 0) (:qty new-item)) 0)
    (without-item shopping-cart-list new-item)
    (with-new-item shopping-cart-list new-item)))