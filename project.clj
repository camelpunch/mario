(defproject mario "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [hiccup "1.0.3"]
                 [com.datomic/datomic-free "0.8.4122"]
                 [clj-time "0.5.1"]
                 [environ "0.4.0"]]
  :plugins [[lein-ring "0.8.5"]
            [lein-expectations "0.0.7"]
            [lein-autoexpect "0.2.5"]
            [lein-environ "0.4.0"]]
  :ring {:handler mario.routes/app}
  :profiles {:dev {:env {:site-base-uri "http://localhost:3000"
                         ; :db-uri "datomic:free://localhost:4334/mario"
                         :db-uri "datomic:mem://mario"
                         :projects-config "test/config.edn"}
                   :dependencies [[ring-mock "0.1.5"]
                                  [expectations "1.4.38"]]}})
