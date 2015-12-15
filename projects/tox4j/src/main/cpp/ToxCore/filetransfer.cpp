#include "ToxCore.h"

#ifdef TOX_VERSION_MAJOR

using namespace core;


/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxFileControl
 * Signature: (IIII)V
 */
TOX_METHOD (void, FileControl,
  jint instanceNumber, jint friendNumber, jint fileNumber, jint control)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_file_control, friendNumber, fileNumber, Enum::valueOf<TOX_FILE_CONTROL> (env, control)
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxFileSeek
 * Signature: (IIII)V
 */
TOX_METHOD (void, FileSeek,
  jint instanceNumber, jint friendNumber, jint fileNumber, jlong position)
{
  return instances.with_instance_ign (env, instanceNumber,
    tox_file_seek, friendNumber, fileNumber, position
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxFileSend
 * Signature: (IIIJ[B)I
 */
TOX_METHOD (jint, FileSend,
  jint instanceNumber, jint friendNumber, jint kind, jlong fileSize, jbyteArray fileId, jbyteArray filename)
{
  auto fileIdData = fromJavaArray (env, fileId);
  auto filenameData = fromJavaArray (env, filename);

  // In Java, we only have 63 bit positive file sizes, so all negative values
  // are streaming.
  if (fileSize < 0)
    fileSize = -1;

  return instances.with_instance_err (env, instanceNumber,
    identity,
    tox_file_send, friendNumber, kind, fileSize, fileIdData, filenameData.data (), filenameData.size ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxFileSendChunk
 * Signature: (III[B)V
 */
TOX_METHOD (void, FileSendChunk,
  jint instanceNumber, jint friendNumber, jint fileNumber, jlong position, jbyteArray chunk)
{
  auto chunkData = fromJavaArray (env, chunk);
  return instances.with_instance_ign (env, instanceNumber,
    tox_file_send_chunk, friendNumber, fileNumber, position, chunkData.data (), chunkData.size ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxFileGetFileId
 * Signature: (II)[B
 */
TOX_METHOD (jbyteArray, FileGetFileId,
  jint instanceNumber, jint friendNumber, jint fileNumber)
{
  uint8_t file_id[TOX_FILE_ID_LENGTH];
  return instances.with_instance_err (env, instanceNumber,
    [env, &file_id] (bool)
      {
        return toJavaArray (env, file_id);
      },
    tox_file_get_file_id, friendNumber, fileNumber, file_id
  );
}

#endif
