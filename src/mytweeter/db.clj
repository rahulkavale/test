(ns mytweeter.db
  (:require [clojure.java.jdbc :as sql]))

(def config (atom (or (System/getenv "DATABASE_URL")
           "postgresql://localhost:5432/mytweeter")))

