(ns mytweeter.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [cheshire.core :refer :all])
  (:require [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.params :as rmp]
            [ring.adapter.jetty :as ring]
            [ring.middleware.json :as middleware])
  (:require [mytweeter.controllers.tweet-controller :as tweet-controller]
            [mytweeter.controllers.user-controller :as user-controller]
            [mytweeter.models.tweet :as tweet]
            [mytweeter.models.hashtags :as hashtags]
            [mytweeter.models.trending :as trending]
            [mytweeter.migrations.migration :as migration])
  (:use ring.util.response)
  (:require [clojure.tools.logging :as log]))

(defn parse-json [httpInputStream]
  (let [body (slurp httpInputStream)]
    (parse-string body)))

(defroutes app-routes
  (POST "/tweets" {tweet :body}
        (let [json (get (parse-json tweet) "tweet")]
          (tweet-controller/create-tweet json)))
  (GET "/tweets" [] (tweet-controller/get-all-tweets))
  (DELETE "/tweets" []
          (tweet-controller/delete-all))
  (POST "/tweets/:tweet_id/retweet" {payload :body params :params}
        (let [user (parse-json payload)
              tweet-id (:tweet_id params)]
          (tweet-controller/retweet tweet-id user)))
  (GET "/tweets/:tweet_id/retweeters" [tweet_id]
       (tweet-controller/get-retweeters tweet_id))
  (GET "/users" [] (user-controller/get-all-users))
  (GET "/users/:user_id/tweets" [user_id]
       (user-controller/get-user-tweets user_id))
  (POST "/users" {request :body}
        (do
          (log/debug "received request to create user")
          (user-controller/create-user (parse-json request))))
  (POST "/follow" {follow-info :body}
        (user-controller/follow (parse-json follow-info))))

(defn wrap-server-exception [handler]
  (fn [request]
    (try
      (println "processing handler")
      (handler request)
      (catch Exception e
        {:status 500 :body {:error "Oops, something went wrong!"}}))))

(defn wrap-middleware [handler]
  (-> handler
      (wrap-defaults api-defaults)
      (middleware/wrap-json-response)))

(def app
  (-> (handler/api app-routes)
      (wrap-server-exception)
      (middleware/wrap-json-response)))

(defn start [port]
  (ring/run-jetty app {:port port}))

(defn -main []
  (hashtags/process-hashtags)
  (trending/process-trending-hashtags)
  (migration/migrate)
  (let [port (Integer. "8000")]
    (start port)))

