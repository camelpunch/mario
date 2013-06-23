(ns mario.views
  (:use [hiccup core page util]))

(defn xml [& args]
  (html (xml-declaration "utf-8") args))

(defn cctray []
  (xml
    [:Projects
     [:Project {:name "My Project"
                :activity "Sleeping"
                :lastBuildStatus "complete"
                :lastBuildTime "2012-12-16T20:06:51-08:00"}]
     ]))

