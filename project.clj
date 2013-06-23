(defproject mario "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [hiccup "1.0.3"]]
  :plugins [[lein-ring "0.8.5"]
            [lein-expectations "0.0.7"]
            [lein-autoexpect "0.2.5"]]
  :ring {:handler mario.routes/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.5"]
                        [expectations "1.4.38"]]}})
