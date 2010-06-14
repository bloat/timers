(ns timers.core
  (:use [clojure.contrib.duck-streams :as ds]
        [clojure.contrib.server-socket :as ss])
  (:import [java.io PushbackReader]
           [java.util Date]
           [java.util.concurrent ScheduledThreadPoolExecutor TimeUnit]
           [java.text SimpleDateFormat]))

(def timers (atom {}))

(def executor (ScheduledThreadPoolExecutor. 1))

(defn timer-fired [action name]
  (swap! timers dissoc name)
  (.exec (Runtime/getRuntime) (.split action "\\s+")))

(defn make-timer [{name :name action :action length :length}]
  (.schedule executor #(timer-fired action name) (long length) TimeUnit/MILLISECONDS))

(defn new-timer [command]
  (swap! timers assoc (:name command) (make-timer command)))

(defn millis-remaining [name]
  (let [timer (@timers name)]
    (if timer
      (.getDelay timer TimeUnit/MILLISECONDS)
      0)))

(defn time-left [command]
  (println (.format (SimpleDateFormat. "mm:ss") (millis-remaining (:name command)))))

(def commands {:new-timer new-timer :time-left time-left})

(defn dispatch [command]
  (((:command command) commands) command))

(defn handle-connection [in out]
  (let [command (read (PushbackReader. (ds/reader in)))]
    (binding [*out* (ds/writer out)]
      (dispatch command)
      (flush))))

(defn -main []
  (ss/create-server 9899 handle-connection))

(comment
  (def server (main-loop))
  (ss/close-server server)
(* 1000 60 25)
  ( (:command {:command :new-timer}) commands)

(new-timer {:name 'hello :length 20000})
(millis-remaining 'hello)
@timers

)



