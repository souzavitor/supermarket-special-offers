(defproject supermarket-special-offers "0.1.0-SNAPSHOT"
  :description "This is a simple project to start studying clojure. We will scan items and define how much the user must pay"
  :url "http://example.com/api/v1/"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [io.pedestal/pedestal.service "0.5.5"]
                 [io.pedestal/pedestal.jetty "0.5.5"]
                 [ch.qos.logback/logback-classic "1.2.3" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.25"]
                 [org.slf4j/jcl-over-slf4j "1.7.25"]
                 [org.slf4j/log4j-over-slf4j "1.7.25"]
                 [org.clojure/data.json "0.2.6"]]
  :main ^:skip-aot supermarket-special-offers.server
  :target-path "target/%s"
  :resource-paths ["config", "resources"]
  :profiles {:dev {:aliases {"run-dev"
                             ["trampoline" "run" "-m" "supermarket-special-offers.server/run-dev"]}
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.5"]]}
             :uberjar {:aot [supermarket-special-offers.server]}})
