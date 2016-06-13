(ns mytweeter.utils)

(defn starts-with? [ch str]
  (= (get str 0) ch))
