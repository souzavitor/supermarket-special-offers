(ns supermarket-special-offers.handlers.shopping_cart
  (:require [ring.util.response :as ring-resp]
            [clojure.pprint :as pp]
            [supermarket-special-offers.controllers.shopping_cart :as shopping_cart_controller])
  (:gen-class))

(defn get-shopping-cart
  [request]
  (ring-resp/response @shopping_cart_controller/shopping-cart))

(defn post-item
  [{:keys [json-params] :as request}]
  (try
    (shopping_cart_controller/add-item json-params)
    (ring-resp/response @shopping_cart_controller/shopping-cart)
    (catch Exception e
      (ring-resp/response {:error {:message (.getMessage e)}}))))

(defn remove-item
  [{:keys [path-params] :as request}]
  (try
    (shopping_cart_controller/remove-item (get path-params :sku))
    (ring-resp/response @shopping_cart_controller/shopping-cart)
    (catch Exception e
      (ring-resp/response {:error {:message (.getMessage e)}}))))

(defn empty-shopping-cart
  [request]
  (shopping_cart_controller/empty-shopping-cart)
  {:status 204 :body nil})