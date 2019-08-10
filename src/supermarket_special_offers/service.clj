(ns supermarket-special-offers.service
  (:require [io.pedestal.interceptor :as interceptor]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [cheshire.core :as json]
            [supermarket-special-offers.handlers.shopping_cart :as shopping_cart_handler]))

;; Including Content-Length and Parsing Body (STREAM) to JSON String
(def content-length-json-body
  (interceptor/interceptor
   {:name ::content-length-json-body
    :leave (fn [context]
             (let [response (:response context)
                   body (:body response)
                   json-response-body (if body (json/generate-string body) "")
                   content-length (count (.getBytes ^String json-response-body))
                   headers (:headers response {})]
               (assoc context
                      :response {:status (:status response)
                                 :body json-response-body
                                 :headers (merge headers
                                                 {"Content-Type" "application/json;charset=UTF-8"
                                                  "Content-Length" (str content-length)})})))}))

(def custom-interceptors [(body-params/body-params) content-length-json-body])

(defn app-status
  [request]
  (ring-resp/response {:status "OK"}))

;; Tabular routes
(def routes
  #{["/api/v1/status" :get (conj custom-interceptors `app-status)]

    ["/api/v1/shopping-cart"
     :get
     (conj custom-interceptors `shopping_cart_handler/get-shopping-cart)]

    ["/api/v1/shopping-cart"
     :delete
     (conj custom-interceptors `shopping_cart_handler/empty-shopping-cart)]

    ["/api/v1/shopping-cart/item/:sku"
     :delete
     (conj custom-interceptors `shopping_cart_handler/remove-item)]

    ["/api/v1/shopping-cart/item"
     :post
     (conj custom-interceptors `shopping_cart_handler/post-item)]})

;; Consumed by json-api.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; ::http/interceptors []
              ::http/routes routes
              ;;::http/allowed-origins ["scheme://host:port"]
              ::http/type :jetty
              ;;::http/host "localhost"
              ::http/port 8080
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        :h2? false
                                        :ssl? false}})