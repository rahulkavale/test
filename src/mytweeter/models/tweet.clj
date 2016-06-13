(ns mytweeter.models.tweet
  (:require [clojure.java.jdbc :as sql])
  (:require [mytweeter.db :as db]))

(defn all []
  (into [] (sql/query db/spec "select * from tweets")))

(defn valid? [tweet]
  (and (not (empty? tweet ))
       (not (nil? (get tweet "user_id")))
       (not (nil? (get tweet "body")))))

(defn insert-tweet [tweet]
  (vec (sql/insert! db/spec
                    :tweets
                    [:body :user_id]
                    [(get tweet "body") (get tweet "user_id")])))

(defn create [tweet]
  (try
    (let [[status] (insert-tweet tweet)]
      (= status 1))
    (catch Exception e
      (println (str "got exception " e))
      false)))

(defn delete-all []
  (sql/delete! db/spec :tweets ["true = ? " true]))
