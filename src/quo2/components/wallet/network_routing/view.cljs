(ns quo2.components.wallet.network-routing.view
  (:require
   [quo2.foundations.colors :as colors]
   [react-native.core :as rn]
   [react-native.gesture :as gesture]
   [react-native.reanimated :as reanimated]
   [oops.core :as oops]
   [reagent.core :as reagent]))

(defn slider
  []
  (let [appearing-opacity (reanimated/use-shared-value 0)
        width             (reanimated/use-shared-value 4)
        height            (reanimated/use-shared-value 32)
        ]

    (rn/use-effect
     (fn []
       (reanimated/animate appearing-opacity 1 300)
       ;; TODO: think how to disappear the component animated
       #(reanimated/animate appearing-opacity 0 300)))

    [rn/touchable-without-feedback {:on-press-in  (fn []
                                                    (reanimated/animate width 8 300)
                                                    (reanimated/animate height 40 300))
                                    :on-press-out (fn []
                                                    (reanimated/animate width 4 300)
                                                    (reanimated/animate height 32 300))}
     [rn/view {:style {:width           8
                       :justify-content :center
                       :align-items     :center
                       :right           -4}}
      [reanimated/view
       {:style (reanimated/apply-animations-to-style
                {:opacity appearing-opacity
                 :height  height
                 :width   width}
                {:background-color colors/white
                 :height           32
                 :width            4
                 :border-radius    4})}]]]))

(defn network-bar []
  (let [show-slider?    (reagent/atom false)]
    (fn [{:keys [max-width selected-width]}]
      [reanimated/view {:style {:width            (if @show-slider?
                                                    max-width
                                                    selected-width)
                                :background-color "#758EEB40"
                                :flex-direction   :row
                                :justify-content  :space-between}}


       [rn/touchable-without-feedback {:on-press #(swap! show-slider? not)}
        [reanimated/view {:style {:width            selected-width
                                  :flex-direction   :row
                                  :justify-content  :flex-end
                                  :background-color "#758EEB"}}
         (when @show-slider?
           [gesture/gesture-detector
            {:gesture (-> (gesture/gesture-pan)
                          (gesture/enabled @show-slider?)
                          (gesture/on-update (fn [event]
                                               (prn (oops/oget event "x"))
                                               nil)))}
            [:f> slider]])]]

       (when @show-slider?
         [reanimated/view
          {:style {:border-left-width 1
                   :border-style      :dashed
                   :border-color      "#758EEB"}}])
       ]
      )))

(defn view []
  [rn/view {:style { ;:margin-horizontal 20
                    :flex-direction    :row
                    :height            64
                    :background-color  :lightgrey
                    :border-radius     18 ;; TODO: check border-radius
                    :overflow          :hidden}}
   [:f> network-bar {:max-width      160
                     :selected-width 120}]

   #_#_#_#_#_[rn/view {:style {:width            "33.3%"
                               :background-color :blue}}]
           [rn/view {:style {:width 1}}]
           [rn/view {:style {:width            "33.3%"
                             :background-color :red}}]
           [rn/view {:style {:width 1}}]
           [rn/view {:style {:width            "33.3%"
                             :background-color :skyblue}}]
   ])
