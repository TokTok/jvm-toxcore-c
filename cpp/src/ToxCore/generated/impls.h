// im.tox.tox4j.impl.jni.ToxCoreJni

JAVA_METHOD (jint, toxIterationInterval,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber, tox_iteration_interval);
}

JAVA_METHOD (jint, toxSelfGetStatus,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber, tox_self_get_status);
}

JAVA_METHOD (void, toxSelfSetNospam,
  jint instanceNumber, jint nospam)
{
  return instances.with_instance_noerr (env, instanceNumber, tox_self_set_nospam, nospam);
}

JAVA_METHOD (jboolean, toxFriendExists,
  jint instanceNumber, jint friendNumber)
{
  return instances.with_instance_noerr (env, instanceNumber, tox_friend_exists, friendNumber);
}

JAVA_METHOD (jint, toxSelfGetNospam,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber, tox_self_get_nospam);
}
