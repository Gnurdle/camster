(ns camster.core
  (:require
   ["fs" :as fs]
   ["tmp" :as tmp]
   ["del" :as del]
   ["path" :as path]
   ["child_process" :as child-process]))

#_ (def ^:private cam-root "/net/cam")

(defn ^:private cannonical-image-dir
  "cannonical path of images directory given a top-level date-stamp"
  [cam-root entry]
  (path/join cam-root entry "images"))

(defn ^:private read-dir-matching
  "return directory entries in a directory that match an re"
  [d re]
  (->> (fs/readdirSync d)
       (js->clj)
       (filter #(re-matches re %))))

(defn ^:private images-in-dir 
  "return image files in the given directory (jpgs)"
  [entry]
  (read-dir-matching entry #"^P.*\.jpg$"))

(defn ^:private create-video
  "convert all the jpgs in a given image-dir to a movie"
  [cam-root outfile image-dir]
  (println "outfile: " outfile "image-dir: " image-dir)
  (let [images (map (fn [ent] (path/join image-dir ent)) (images-in-dir image-dir))
        tmpobj (tmp/fileSync)
        tmpname (.-name tmpobj)
        tmpfd (fs/openSync tmpname "w" 0666)]
    
    ;; concat filter input file for ffmpg
    (doseq [image images]
      (fs/writeSync tmpfd (str "file " image "\n")))    
    (fs/closeSync tmpfd)
    
    ;; run ffmpeg
    (let [args ["-f" "concat" "-y" "-safe" "0" "-i" tmpname
                "-c:v" "libx264" "-vf" "fps=25"
                (path/join cam-root outfile)]
          pr (child-process/spawnSync "ffmpeg" (clj->js args) (clj->js {:stdio "inherit"}))]

      ;; scrub temp file
      ((.-removeCallback tmpobj))

      ;; propogate error
      (when-let [err (.-error pr)]
        (throw (ex-info "ffmpeg failed" err))))))

(defn main
  "turn all but the last images directory into a single video apiece"
  [& cam-roots]
  (println "[camster]")
  (doseq [cam-root (map path/resolve cam-roots)]
    (println "start processing for: " cam-root)
    (let [todo-dirs (->> (read-dir-matching cam-root #"[0-9]{8}")
                         (sort)
                         (butlast))]
      (doseq [todo-dir todo-dirs]
        (println "starting: " todo-dir)
        (let [image-path (cannonical-image-dir cam-root todo-dir)
              todo-path (path/join cam-root todo-dir)]
          (create-video cam-root (str "daily-" todo-dir ".mp4") image-path)
          (del/sync todo-path (clj->js {:force true}))))
      (println "camster is out..."))))

