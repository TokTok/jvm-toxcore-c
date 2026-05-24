package im.tox.tox4j.av.callbacks

interface ToxAvEventListener<ToxCoreState> :
    AudioBitRateCallback<ToxCoreState>,
    AudioDataCallback<ToxCoreState>,
    AudioReceiveFrameCallback<ToxCoreState>,
    CallCallback<ToxCoreState>,
    CallStateCallback<ToxCoreState>,
    VideoBitRateCallback<ToxCoreState>,
    VideoReceiveFrameCallback<ToxCoreState>
