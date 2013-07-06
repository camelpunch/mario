(ns mario.views
  (:use [hiccup core page util]))

(defn xml [& args]
  (html (xml-declaration "utf-8") args))

(defn cctray []
  (xml
    [:Projects
     [:Project {:name "Successful Project"
                :activity "Sleeping"
                :lastBuildStatus "Success"
                :lastBuildTime "2012-12-16T20:06:51-08:00"}]
     [:Project {:name "Failing Project"
                :activity "Sleeping"
                :lastBuildStatus "Unknown"
                :lastBuildTime "2012-12-16T20:06:51-08:00"}]
     ]))

