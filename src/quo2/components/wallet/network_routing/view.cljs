(ns quo2.components.wallet.network-routing.view
  (:require
   [quo2.foundations.colors :as colors]
   [react-native.core :as rn]
   [react-native.gesture :as gesture]
   [react-native.reanimated :as reanimated]
   [oops.core :as oops]
   [reagent.core :as reagent]))

(defn slider
  [slider-width slider-height]
  (let [appearing-opacity (reanimated/use-shared-value 0)]
    (rn/use-effect
     (fn []
       (reanimated/animate appearing-opacity 1 300)
       ;; TODO: think how to disappear the component animated
       #(reanimated/animate appearing-opacity 0 300)))

    [rn/view {:style {:width            40
                      :background-color :transparent
                      :justify-content  :center
                      :align-items      :center
                      :right            -20}}
     [reanimated/view
      {:style (reanimated/apply-animations-to-style
               {:opacity appearing-opacity
                :height  slider-height
                :width   slider-width}
               {:background-color colors/white
                :height           32
                :width            4
                :border-radius    4})}]]))

(defn network-bar [_]
  (let [show-slider?          (reagent/atom false)
        initial-gesture-width (reagent/atom 0)]
    (fn [{:keys [max-amount amount color on-press translate-x on-top? on-selection-end
                 ;;
                 total-width total-amount]}]
      ;; TODO: make shared values reactive with an effect
      (let [slider-width  (reanimated/use-shared-value 4)
            slider-height (reanimated/use-shared-value 32)
            ;;
            bar-width     (reanimated/interpolate amount [0 total-amount] [0 total-width])
            ]
        [rn/touchable-without-feedback {:on-press (fn []
                                                    (on-press)
                                                    (reset! show-slider? true))}
         [reanimated/view
          {:style (reanimated/apply-animations-to-style
                   {:width     bar-width
                    :transform [{:translate-x translate-x}]}
                   {:position         :relative
                    :max-width        max-amount
                    :flex-direction   :row
                    :justify-content  :flex-end
                    :background-color color
                    :z-index          (if on-top? 1 0)})}
          (when @show-slider?
            [gesture/gesture-detector
             {:gesture (-> (gesture/gesture-pan)
                           (gesture/enabled @show-slider?)
                           (gesture/on-begin
                            (fn [_]
                              (reanimated/animate slider-width 8 300)
                              (reanimated/animate slider-height 40 300)
                              ;;
                              (reset! initial-gesture-width
                                      (reanimated/get-shared-value amount))))
                           (gesture/on-update
                            (fn [event]
                              (let [new-pos (utils.number/value-in-range
                                             (+ (oops/oget event "translationX") @initial-gesture-width)
                                             1
                                             max-amount)]
                                (reanimated/set-shared-value amount new-pos))
                              nil))
                           (gesture/on-finalize
                            (fn [_]
                              (reanimated/animate slider-width 4 300)
                              (reanimated/animate slider-height 32 300)
                              (js/setTimeout on-selection-end 3000)
                              (js/setTimeout (fn []
                                               (reset! show-slider? false))
                                             300)
                              )))}
             [:f> slider slider-width slider-height]])]])

      )))

#_(defn max-amount-indicator []
    [reanimated/view {:style {:position           :absolute
                              :top                0
                              :bottom             0
                              :left               0
                              :background-color   "#1A770230"
                              :width              250
                              :border-right-width 1
                              :border-style       :dashed
                              :border-right-color "#1A7702"}}])


(defn aux-comp []
  (let [selected-network (reagent/atom nil)]
    (fn [{:keys [total-amount networks total-width]}]
      (let [networks-indexed (map-indexed
                              (fn [idx {:keys [amount] :as network}]
                                (assoc network
                                  :idx idx
                                  :amount-shared-value (reanimated/use-shared-value amount) ;(reanimated/use-shared-value (* amount (/ total-width total-amount)))
                                  :translate-x-shared-value (reanimated/use-shared-value 0)))
                                          networks)]
        ;; TODO: do it
        #_[reanimated/view {:style {:position           :absolute
                                    :top                0
                                    :bottom             0
                                    :left               0
                                    :background-color   "#1A770230"
                                    :width              250 ;; according to the selected network
                                    :border-right-width 1
                                    :border-style       :dashed
                                    :border-right-color "#1A7702"}}]
        [:<>
         (doall
          (map-indexed
           (fn [idx {:keys [color max-amount amount-shared-value translate-x-shared-value]}]
             ^{:key (str "network-bar-" idx)}
             [:f> network-bar {:max-amount   max-amount
                               :amount       amount-shared-value
                               :total-width  total-width
                               :total-amount total-amount
                               :color        color
                               :translate-x  translate-x-shared-value
                               :on-press     (fn []
                                               (let [[previous-bars next-bars]   (split-at (inc idx) networks-indexed)
                                                     next-bars-shared-values-neg (map #(- (reanimated/get-shared-value (:amount-shared-value %)))
                                                                                      next-bars)]
                                                 (prn next-bars)
                                                 (doseq [[idx {:keys [translate-x-shared-value] :as network-bar}] (map-indexed (fn [idx bar] [idx bar])
                                                                                                                               next-bars)]
                                                   (reanimated/animate translate-x-shared-value
                                                                       (* (+ (->> next-bars-shared-values-neg
                                                                                  (drop idx)
                                                                                  (reduce +))
                                                                             (- (reanimated/get-shared-value amount-shared-value)))
                                                                          (/ total-width total-amount)
                                                                          )
                                                                       700))

                                                 #_#_(reanimated/animate translate-2
                                                                         (+ (- (reanimated/get-shared-value selected-width-1))
                                                                            (- (reanimated/get-shared-value selected-width-2))
                                                                            (- (- 160 (reanimated/get-shared-value selected-width-1))))
                                                                         700)
                                                         (reanimated/animate translate-3
                                                                             (+ (- (reanimated/get-shared-value selected-width-1))
                                                                                (- (reanimated/get-shared-value selected-width-2))
                                                                                (- (reanimated/get-shared-value selected-width-3))
                                                                                (- (- 160 (reanimated/get-shared-value selected-width-1))))
                                                                             700)
                                                 (reset! selected-network :blue)))

                               #_#_#_#_:on-top? (= @selected-network :blue)
                                       :on-selection-end (fn []
                                                           (reanimated/animate translate-1
                                                                               (- (reanimated/get-shared-value selected-width-1))
                                                                               400)
                                                           (js/setTimeout
                                                            (fn []

                                                              (reanimated/animate translate-1 0 600)
                                                              (reanimated/animate translate-2 0 600)
                                                              (reanimated/animate translate-3 0 600)
                                                              (js/setTimeout #(reset! selected-network nil) 600))
                                                            400))}])
           networks-indexed))
         ]

        ;; TODO: add white left or right border
        #_#_#_[:f> network-bar {:max-width        160
                                :selected-width   selected-width-1
                                :color            "#758EEB"
                                :translate-x      translate-1
                                :on-press         (fn []
                                                    (reanimated/animate translate-2
                                                                        (+ (- (reanimated/get-shared-value selected-width-1))
                                                                           (- (reanimated/get-shared-value selected-width-2))
                                                                           (- (- 160 (reanimated/get-shared-value selected-width-1))))
                                                                        700)
                                                    (reanimated/animate translate-3
                                                                        (+ (- (reanimated/get-shared-value selected-width-1))
                                                                           (- (reanimated/get-shared-value selected-width-2))
                                                                           (- (reanimated/get-shared-value selected-width-3))
                                                                           (- (- 160 (reanimated/get-shared-value selected-width-1))))
                                                                        700)
                                                    (reset! selected-network :blue))
                                :on-top?          (= @selected-network :blue)
                                :on-selection-end (fn []
                                                    (reanimated/animate translate-1
                                                                        (- (reanimated/get-shared-value selected-width-1))
                                                                        400)
                                                    (js/setTimeout
                                                     (fn []

                                                       (reanimated/animate translate-1 0 600)
                                                       (reanimated/animate translate-2 0 600)
                                                       (reanimated/animate translate-3 0 600)
                                                       (js/setTimeout #(reset! selected-network nil) 600))
                                                     400))}]

                [:f> network-bar {:max-width        100
                                  :selected-width   selected-width-2
                                  :color            "#E76E6E"
                                  :translate-x      translate-2
                                  :on-press         (fn []
                                                      (reanimated/animate translate-1
                                                                          (+ (- (reanimated/get-shared-value selected-width-1))
                                                                             ;   (- 100 (reanimated/get-shared-value selected-width-2))
                                                                             )
                                                                          600)
                                                      (reanimated/animate translate-2
                                                                          (- (reanimated/get-shared-value selected-width-1))
                                                                          600)
                                                      (reanimated/animate translate-3
                                                                          (+ (- (reanimated/get-shared-value selected-width-1))
                                                                             (- (reanimated/get-shared-value selected-width-2))
                                                                             (- (reanimated/get-shared-value selected-width-3))
                                                                             (- (- 100 (reanimated/get-shared-value selected-width-2))))
                                                                          600)
                                                      (reset! selected-network :red))
                                  :on-top?          (= @selected-network :red)
                                  :on-selection-end (fn []
                                                      (reanimated/animate translate-2
                                                                          (- (- (reanimated/get-shared-value selected-width-1))
                                                                             (reanimated/get-shared-value selected-width-2))
                                                                          400)
                                                      (js/setTimeout
                                                       (fn []
                                                         (reanimated/animate translate-1 0 600)
                                                         (reanimated/animate translate-2 0 600)
                                                         (reanimated/animate translate-3 0 600)
                                                         (js/setTimeout #(reset! selected-network nil) 600))
                                                       400))}]
                [:f> network-bar {:max-width        150
                                  :selected-width   selected-width-3
                                  :color            "#6BD5F0"
                                  :translate-x      translate-3
                                  :on-press         (fn []
                                                      (reanimated/animate translate-1
                                                                          (- (reanimated/get-shared-value selected-width-1))
                                                                          600)
                                                      (reanimated/animate translate-2
                                                                          (+ (- (reanimated/get-shared-value selected-width-1))
                                                                             (- (reanimated/get-shared-value selected-width-2)))
                                                                          600)
                                                      (reanimated/animate translate-3
                                                                          (+ (- (reanimated/get-shared-value selected-width-1))
                                                                             (- (reanimated/get-shared-value selected-width-2)))
                                                                          600)
                                                      (reset! selected-network :light-blue))
                                  :on-top?          (= @selected-network :light-blue)
                                  :on-selection-end (fn []
                                                      (reanimated/animate translate-3
                                                                          (- (- (reanimated/get-shared-value selected-width-1))
                                                                             (reanimated/get-shared-value selected-width-2)
                                                                             (reanimated/get-shared-value selected-width-3))
                                                                          400)
                                                      (js/setTimeout
                                                       (fn []
                                                         (reanimated/animate translate-1 0 600)
                                                         (reanimated/animate translate-2 0 600)
                                                         (reanimated/animate translate-3 0 600)
                                                         (js/setTimeout #(reset! selected-network nil) 600))
                                                       400))}]

        #_#_#_#_#_[rn/view {:style {:width            "33.3%"
                                    :background-color :blue}}]
                [rn/view {:style {:width 1}}]
                [rn/view {:style {:width            "33.3%"
                                  :background-color :red}}]
                [rn/view {:style {:width 1}}]
                [rn/view {:style {:width            "33.3%"
                                  :background-color :skyblue}}]
        )))

  )

(defn view-internal [_]
  (let [total-width (reagent/atom nil)]
    (fn [params]
      [rn/view {:style     {;:margin-horizontal 20
                            :flex-direction   :row
                            :height           64
                            :background-color :lightgrey
                            :border-radius    20 ;; TODO: check border-radius
                            :overflow         :hidden}
                :on-layout #(reset! total-width (oops/oget % "nativeEvent.layout.width"))}

       (when @total-width
         [:f> aux-comp (assoc params :total-width @total-width)])

       ;; TODO: do it
       #_[reanimated/view {:style {:position           :absolute
                                   :top                0
                                   :bottom             0
                                   :left               0
                                   :background-color   "#1A770230"
                                   :width              250 ;; according to the selected network
                                   :border-right-width 1
                                   :border-style       :dashed
                                   :border-right-color "#1A7702"}}]
       #_(doall
          (map (fn [{:keys [color max-amount amount-shared-value translate-x-shared-value]}]
                 (println
                  (reanimated/get-shared-value amount-shared-value)
                  " * "
                  "( " total-amount " / " @total-width " ) = "
                  (* (reanimated/get-shared-value amount-shared-value)
                     (/ @total-width total-amount)))
                 ^{:key (random-uuid)}
                 [:f> network-bar {:max-width   max-amount
                                   :amount      (reanimated/set-shared-value
                                                 amount-shared-value
                                                 (if @total-width
                                                   (* (reanimated/get-shared-value amount-shared-value)
                                                      (/ @total-width total-amount))
                                                   20 ;amount-shared-value
                                                   ))
                                   :color       color
                                   :translate-x translate-x-shared-value

                                   #_#_#_#_#_#_:on-press (fn []
                                                           (reanimated/animate translate-2
                                                                               (+ (- (reanimated/get-shared-value selected-width-1))
                                                                                  (- (reanimated/get-shared-value selected-width-2))
                                                                                  (- (- 160 (reanimated/get-shared-value selected-width-1))))
                                                                               700)
                                                           (reanimated/animate translate-3
                                                                               (+ (- (reanimated/get-shared-value selected-width-1))
                                                                                  (- (reanimated/get-shared-value selected-width-2))
                                                                                  (- (reanimated/get-shared-value selected-width-3))
                                                                                  (- (- 160 (reanimated/get-shared-value selected-width-1))))
                                                                               700)
                                                           (reset! selected-network :blue))
                                           :on-top? (= @selected-network :blue)
                                           :on-selection-end (fn []
                                                               (reanimated/animate translate-1
                                                                                   (- (reanimated/get-shared-value selected-width-1))
                                                                                   400)
                                                               (js/setTimeout
                                                                (fn []

                                                                  (reanimated/animate translate-1 0 600)
                                                                  (reanimated/animate translate-2 0 600)
                                                                  (reanimated/animate translate-3 0 600)
                                                                  (js/setTimeout #(reset! selected-network nil) 600))
                                                                400))}])
               networks-indexed))

       ;; TODO: add white left or right border
       #_#_#_[:f> network-bar {:max-width        160
                               :selected-width   selected-width-1
                               :color            "#758EEB"
                               :translate-x      translate-1
                               :on-press         (fn []
                                                   (reanimated/animate translate-2
                                                                       (+ (- (reanimated/get-shared-value selected-width-1))
                                                                          (- (reanimated/get-shared-value selected-width-2))
                                                                          (- (- 160 (reanimated/get-shared-value selected-width-1))))
                                                                       700)
                                                   (reanimated/animate translate-3
                                                                       (+ (- (reanimated/get-shared-value selected-width-1))
                                                                          (- (reanimated/get-shared-value selected-width-2))
                                                                          (- (reanimated/get-shared-value selected-width-3))
                                                                          (- (- 160 (reanimated/get-shared-value selected-width-1))))
                                                                       700)
                                                   (reset! selected-network :blue))
                               :on-top?          (= @selected-network :blue)
                               :on-selection-end (fn []
                                                   (reanimated/animate translate-1
                                                                       (- (reanimated/get-shared-value selected-width-1))
                                                                       400)
                                                   (js/setTimeout
                                                    (fn []

                                                      (reanimated/animate translate-1 0 600)
                                                      (reanimated/animate translate-2 0 600)
                                                      (reanimated/animate translate-3 0 600)
                                                      (js/setTimeout #(reset! selected-network nil) 600))
                                                    400))}]

               [:f> network-bar {:max-width        100
                                 :selected-width   selected-width-2
                                 :color            "#E76E6E"
                                 :translate-x      translate-2
                                 :on-press         (fn []
                                                     (reanimated/animate translate-1
                                                                         (+ (- (reanimated/get-shared-value selected-width-1))
                                                                            ;   (- 100 (reanimated/get-shared-value selected-width-2))
                                                                            )
                                                                         600)
                                                     (reanimated/animate translate-2
                                                                         (- (reanimated/get-shared-value selected-width-1))
                                                                         600)
                                                     (reanimated/animate translate-3
                                                                         (+ (- (reanimated/get-shared-value selected-width-1))
                                                                            (- (reanimated/get-shared-value selected-width-2))
                                                                            (- (reanimated/get-shared-value selected-width-3))
                                                                            (- (- 100 (reanimated/get-shared-value selected-width-2))))
                                                                         600)
                                                     (reset! selected-network :red))
                                 :on-top?          (= @selected-network :red)
                                 :on-selection-end (fn []
                                                     (reanimated/animate translate-2
                                                                         (- (- (reanimated/get-shared-value selected-width-1))
                                                                            (reanimated/get-shared-value selected-width-2))
                                                                         400)
                                                     (js/setTimeout
                                                      (fn []
                                                        (reanimated/animate translate-1 0 600)
                                                        (reanimated/animate translate-2 0 600)
                                                        (reanimated/animate translate-3 0 600)
                                                        (js/setTimeout #(reset! selected-network nil) 600))
                                                      400))}]
               [:f> network-bar {:max-width        150
                                 :selected-width   selected-width-3
                                 :color            "#6BD5F0"
                                 :translate-x      translate-3
                                 :on-press         (fn []
                                                     (reanimated/animate translate-1
                                                                         (- (reanimated/get-shared-value selected-width-1))
                                                                         600)
                                                     (reanimated/animate translate-2
                                                                         (+ (- (reanimated/get-shared-value selected-width-1))
                                                                            (- (reanimated/get-shared-value selected-width-2)))
                                                                         600)
                                                     (reanimated/animate translate-3
                                                                         (+ (- (reanimated/get-shared-value selected-width-1))
                                                                            (- (reanimated/get-shared-value selected-width-2)))
                                                                         600)
                                                     (reset! selected-network :light-blue))
                                 :on-top?          (= @selected-network :light-blue)
                                 :on-selection-end (fn []
                                                     (reanimated/animate translate-3
                                                                         (- (- (reanimated/get-shared-value selected-width-1))
                                                                            (reanimated/get-shared-value selected-width-2)
                                                                            (reanimated/get-shared-value selected-width-3))
                                                                         400)
                                                     (js/setTimeout
                                                      (fn []
                                                        (reanimated/animate translate-1 0 600)
                                                        (reanimated/animate translate-2 0 600)
                                                        (reanimated/animate translate-3 0 600)
                                                        (js/setTimeout #(reset! selected-network nil) 600))
                                                      400))}]

       #_#_#_#_#_[rn/view {:style {:width            "33.3%"
                                   :background-color :blue}}]
               [rn/view {:style {:width 1}}]
               [rn/view {:style {:width            "33.3%"
                                 :background-color :red}}]
               [rn/view {:style {:width 1}}]
               [rn/view {:style {:width            "33.3%"
                                 :background-color :skyblue}}]
       ])))

(defn view [params]
  [:f> view-internal params])
