package im.tox.tox4j.av.callbacks

interface ToxAvEventListener<ToxCoreState> :
    CallCallback<ToxCoreState>,
    CallStateCallback<ToxCoreState>,
    AudioBitRateCallback<ToxCoreState>,
    VideoBitRateCallback<ToxCoreState>,
    AudioReceiveFrameCallback<ToxCoreState>,
    VideoReceiveFrameCallback<ToxCoreState>
