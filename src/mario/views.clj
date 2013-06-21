(ns mario.views
  (:use [hiccup core page util]))

(defn xml [& args]
  (html (xml-declaration "utf-8") args))

(defn index-page []
  (xml
    [:Projects
     [:Project {:name "My Project"}]]))

