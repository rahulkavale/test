(ns mytweeter.test-helper
  (:require  [midje.sweet :as midje]
             [cheshire.core :refer :all]
             [clojure.java.jdbc :as sql])
  (:require  [mytweeter.db :as db]))


(defn parse [response]
  (parse-string (:body response)))

(defn delete-all-rows [table-name]
  (sql/execute! @db/config [(str "delete from " table-name)]))

(defn clear-tables []
  (println "Deleting all the table")
  (doall (map delete-all-rows '("users" "tweets" "retweets"))))

