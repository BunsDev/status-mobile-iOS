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
    (fn [{:keys [max-width selected-width color on-press translate-x on-top?]}]
      (let [slider-width  (reanimated/use-shared-value 4)
            slider-height (reanimated/use-shared-value 32)
            ;;
            ]
        [rn/touchable-without-feedback {:on-press (fn []
                                                    (on-press)
                                                    (swap! show-slider? not))}
         [reanimated/view
          {:style (reanimated/apply-animations-to-style
                   {:width     selected-width
                    :transform [{:translate-x translate-x}]}
                   {:max-width        max-width
                    :flex-direction   :row
                    :justify-content  :flex-end
                    :background-color color
                    :z-index          (when on-top? 1)})}
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
                                      (reanimated/get-shared-value selected-width))))
                           (gesture/on-update
                            (fn [event]
                              (let [new-pos (+ (oops/oget event "translationX")
                                               @initial-gesture-width
                                               ;(oops/oget event "x")
                                               ;(reanimated/get-shared-value selected-width)
                                               )]
                                ;(prn (oops/oget event "x"))
                                #_(println (reanimated/get-shared-value selected-width)
                                           " + "
                                           (oops/oget event "x")
                                           " = "
                                           new-pos)
                                (reanimated/set-shared-value selected-width new-pos))
                              nil))
                           (gesture/on-finalize
                            (fn [_]
                              (reanimated/animate slider-width 4 300)
                              (reanimated/animate slider-height 32 300)
                              #_(js/setTimeout (fn []
                                               )
                                             3000))))}
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

(defn view-internal []
  (let [selected-network (reagent/atom nil)]
    (fn []
      (let [
            selected-width   (reanimated/use-shared-value 110)
            selected-width-2 (reanimated/use-shared-value 60)
            selected-width-3 (reanimated/use-shared-value 90)
            ;;
            translate-1 (reanimated/use-shared-value 0)
            translate-2 (reanimated/use-shared-value 0)
            translate-3 (reanimated/use-shared-value 0)
            ]
        [rn/view {:style {;:margin-horizontal 20
                          :flex-direction   :row
                          :height           64
                          :background-color :lightgrey
                          :border-radius    20 ;; TODO: check border-radius
                          :overflow         :hidden}}

         [reanimated/view {:style {:position           :absolute
                                   :top                0
                                   :bottom             0
                                   :left               0
                                   :background-color   "#1A770230"
                                   :width              250 ;; according to the selected network
                                   :border-right-width 1
                                   :border-style       :dashed
                                   :border-right-color "#1A7702"}}]

         [:f> network-bar {:max-width           160
                           :selected-width      selected-width
                           :color               "#758EEB"
                           :translate-x         translate-1
                           :on-press            (fn []
                                                  (reanimated/animate translate-2 (+ -110 -60) 600)
                                                  (reanimated/animate translate-3 (+ -110 -60 -90) 600)
                                                  (reset! selected-network :blue))
                           :on-top?             (= @selected-network :blue)
                           :on-calculate-finish (fn []
                                                  (reanimated/animate translate-2 (+ -110 -60) 600)
                                                  (reanimated/animate translate-3 (+ -110 -60 -90) 600)
                                                  (reset! selected-network nil))}]
         [:f> network-bar {:max-width      100
                           :selected-width selected-width-2
                           :color          "#E76E6E"
                           :translate-x    translate-2
                           :on-press       (fn []
                                             (reanimated/animate translate-1 -110 600)
                                             (reanimated/animate translate-2 -110 600)
                                             (reanimated/animate translate-3 (+ -110 -60 -150) 600)
                                             (reset! selected-network :red))
                           :on-top?        (= @selected-network :red)}]
         [:f> network-bar {:max-width      150
                           :selected-width selected-width-3
                           :color          "#6BD5F0"
                           :translate-x    translate-3
                           :on-press       (fn []
                                             (reanimated/animate translate-1 -110 600)
                                             (reanimated/animate translate-2 -170 600)
                                             (reanimated/animate translate-3 -170 600)
                                             (reset! selected-network :light-blue))
                           :on-top?        (= @selected-network :light-blue)}]



         #_#_#_#_#_[rn/view {:style {:width            "33.3%"
                                     :background-color :blue}}]
                 [rn/view {:style {:width 1}}]
                 [rn/view {:style {:width            "33.3%"
                                   :background-color :red}}]
                 [rn/view {:style {:width 1}}]
                 [rn/view {:style {:width            "33.3%"
                                   :background-color :skyblue}}]
         ]))))

(defn view []
  [:f> view-internal])
