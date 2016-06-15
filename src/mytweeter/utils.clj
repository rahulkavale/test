(ns mytweeter.utils)

(defn starts-with? [ch str]
  "Simple utility to check if first char of string matches the given char"
  (= (get str 0) ch))
