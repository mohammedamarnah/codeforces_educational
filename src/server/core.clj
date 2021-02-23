(ns server.core
  (:require [server.routes :refer [app]]
            [org.httpkit.server :refer [run-server]])
  (:gen-class))

(defonce server (atom nil))
(defonce PORT 5050)

(defn start-server
  "Starts a server on the defined port."
  []
  (println "Starting server on port " PORT)
  (run-server app {:port PORT}))

(defn -main
  []
  (start-server))

