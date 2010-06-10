(ns timers.core
  (:use [clojure.contrib.duck-streams :as ds]
        [clojure.contrib.server-socket :as ss]))

(defn do-stuff [in out]
  (prn (.readLine (ds/reader in))))

(defn main-loop []
  (ss/create-server 9899 do-stuff))

(comment
  (def server (ss/create-server 9899 do-stuff))
  (ss/close-server server))



