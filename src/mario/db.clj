(ns mario.db
  (:require [environ.core :refer [env]]
            [taoensso.carmine :as car :refer [wcar]]))

(def server1-conn {:pool {} :spec (env :redis)})
(defmacro wcar* [& body] `(car/wcar server1-conn ~@body))

(defn wipe [] (wcar* (car/flushdb)))

(defn add-job [job-name]
  (wcar* (car/hmset "jobs" job-name {:job/name job-name
                                     :job/builds []})))

(defn job [job-name]
  "Returns a job by name"
  (first (wcar* (car/hmget "jobs" job-name))))

(defn all-jobs []
  (take-nth 2 (next (wcar* (car/hgetall "jobs")))))

(defn- next-id-for [key] (wcar* (car/incr (str "id." key))))

(defn build-started [job-name]
  (let [job (job job-name)]
    (wcar*
      (car/hmset "jobs" job-name (merge job {:job/builds [{:build/name 1}]})))
    1))

(defn build-failed [job-name build]
  (if-let [job (job job-name)]
    (if (>= (count (:job/builds job)) (Integer. build))
      (wcar*
        (car/hmset "jobs" job-name (merge job {:job/builds [{:build/name 1
                                                             :build/result "failure"}]}))))))

