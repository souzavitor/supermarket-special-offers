(ns supermarket-special-offers.server
  (:require [io.pedestal.http :as server]
            [io.pedestal.http.route :as route]
            [supermarket-special-offers.logic.special_offers :as special_offers_logic]
            [supermarket-special-offers.service :as service])
  (:gen-class))


;; This is an adapted service map, that can be started and stopped
;; From the REPL you can call server/start and server/stop on this service
(defonce runnable-service (server/create-server service/service))

(defn run-dev
  "The entry-point for 'lein run-dev'"
  [& args]
  (println "\nCreating your [DEV] server... :)")
  (-> service/service ;; start with production configuration
      (merge {:env :dev
              ::server/join? false
              ;; Routes can be a function that resolve routes,
              ;;  we can use this to set the routes to be reloadable
              ::server/routes #(route/expand-routes (deref #'service/routes))
              ;; all origins are allowed in dev mode
              ::server/allowed-origins {:creds true :allowed-origins (constantly true)}})
      ;; Wire up interceptor chains
      server/default-interceptors
      server/dev-interceptors
      server/create-server
      server/start))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (special_offers_logic/create-offers-from-file "offers.txt")
  (println "\nCreating your server... :D")
  (server/start runnable-service))
