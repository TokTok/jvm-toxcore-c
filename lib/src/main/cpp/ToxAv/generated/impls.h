// im.tox.tox4j.impl.jni.ToxAvJni

JAVA_METHOD (jint, toxavIterationInterval,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    toxav_iteration_interval
  );
}


JAVA_METHOD (jbyteArray, toxavIterate,
  jint instanceNumber)
{
  return instances.with_instance (env, instanceNumber,
    [=] (ToxAV *self, Events &events) -> jbyteArray
      {
        toxav_iterate(self);
        if (events.ByteSizeLong () == 0)
          return nullptr;

        std::vector<char> buffer (events.ByteSizeLong ());
        if (!events.SerializeToArray (buffer.data (), buffer.size ()))
          return nullptr;
        events.Clear ();

        return toJavaArray (env, buffer);
      }
  );
}

JAVA_METHOD (void, toxavCall,
  jint instanceNumber, jint friendNumber, jint audioBitRate, jint videoBitRate)
{
  return instances.with_instance_ign (env, instanceNumber,
    toxav_call, friendNumber, audioBitRate, videoBitRate
  );
}

JAVA_METHOD (void, toxavAnswer,
  jint instanceNumber, jint friendNumber, jint audioBitRate, jint videoBitRate)
{
  return instances.with_instance_ign (env, instanceNumber,
    toxav_answer, friendNumber, audioBitRate, videoBitRate
  );
}

JAVA_METHOD (void, toxavCallControl,
  jint instanceNumber, jint friendNumber, jint control)
{
  return instances.with_instance_ign (env, instanceNumber,
    toxav_call_control, friendNumber, Enum::valueOf<Toxav_Call_Control> (env, control)
  );
}

// toxavAudioSendFrame is hand-written.

JAVA_METHOD (void, toxavAudioSetBitRate,
  jint instanceNumber, jint friendNumber, jint bitRate)
{
  return instances.with_instance_ign (env, instanceNumber,
    toxav_audio_set_bit_rate, friendNumber, bitRate
  );
}

// toxavVideoSendFrame is hand-written.

JAVA_METHOD (void, toxavVideoSetBitRate,
  jint instanceNumber, jint friendNumber, jint bitRate)
{
  return instances.with_instance_ign (env, instanceNumber,
    toxav_video_set_bit_rate, friendNumber, bitRate
  );
}

