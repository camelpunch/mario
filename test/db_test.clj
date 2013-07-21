(ns db-test
  (:require [expectations :refer :all]
            [clojure.set :refer :all]
            [mario.db :as db]
            [test-helpers :as t]
            [clj-time.core :as ctime]))

(db/init-db)

;; naming allows finding
(expect-let [somename (doto (t/uuid) db/name-job)]
            somename (->> (db/item :job/name somename) :job/name))

;; and finding all
(expect-let [name1 (doto (t/uuid) db/name-job)
             name2 (doto (t/uuid) db/name-job)
             all-names (->> (db/all-jobs) (map :job/name) set)]
            true (subset? #{name1 name2} all-names))

;; can add a build to a job id (even if it doesn't exist)
(expect-let [buildname (doto (t/uuid) db/build-started)]
            buildname (->> (db/item :build/name buildname) :build/name))

