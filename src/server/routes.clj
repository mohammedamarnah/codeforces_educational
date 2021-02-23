(ns server.routes
  (:require [compojure.core :refer [defroutes GET]]
            [clojure.string :refer [split]]
            [org.httpkit.client :as http]
            [compojure.route :as route]
            [jsonista.core :as json]))

(defonce contest-url "https://codeforces.com/contest/")

(defonce contests-url "https://codeforces.com/api/contest.list")
(defonce user-url "https://codeforces.com/api/user.status?handle=")

(defonce not-found
  {:status 404
   :headers {"Content-Type" "application/text"}
   :body "Not Found..."})

(defonce error-resp
  {:status 0
   :body ""})

(defn parse-submissions
  [submissions contest-ids]
  (let [submissions (-> submissions (json/read-value) (get "result"))
        contains-sub? (partial contains? (set contest-ids))]
    (filter #(and
               (= (get % "verdict") "OK")
               (contains-sub? (get % "contestId"))) submissions)))

(defn get-user-submissions
  [username contest-ids]
  (let [{:keys [status body error]} @(http/get (str user-url username))]
    (if error
      (merge error-resp {:status status
                         :error error})
      (parse-submissions body contest-ids))))

(defn get-educational-contests
  [contests]
  (let [contests (-> contests (json/read-value) (get "result"))]
    (filter #(= (first (split (get % "name") #" ")) "Educational") contests)))

(defn download-contests
  []
  (let [{:keys [status body error]} @(http/get contests-url)]
    (if error
      (merge error-resp {:status status
                         :error error})
      (get-educational-contests body))))

(defn construct-result
  [user-submissions contest-ids]
  (let [sub-map (reduce #(update %1 (get %2 "contestId") conj %2)
                        {} user-submissions)
        contest-ids (sort contest-ids)]
    (map (fn [cid]
           (let [subms (or (get sub-map cid) [])
                 solved (mapv #(get-in % ["problem" "index"]) subms)
                 solved (-> solved distinct sort vec)]
             (str contest-url cid " --- " solved))) contest-ids)))

(defn handle-user
  [request]
  (let [username (-> request :params :username)
        educational-contests (download-contests)
        contest-ids (map #(get % "id") educational-contests)
        user-submissions (get-user-submissions username contest-ids)
        final-result (construct-result user-submissions contest-ids)]
    {:status 202
     :headers {"Content-Type" "application/json"}
     :body (json/write-value-as-string final-result)}))

(defroutes app
  (GET "/" [] {:status 200
               :headers {"Content-Type" "application/json"}
               :body (json/write-value-as-string {:hello "world"})})
  (GET "/contests/:username" [] handle-user)
  (route/not-found not-found))

