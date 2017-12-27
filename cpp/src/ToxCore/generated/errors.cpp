#include "../ToxCore.h"

HANDLE ("Bootstrap", BOOTSTRAP)
{
  switch (error)
    {
    success_case (BOOTSTRAP);
    failure_case (BOOTSTRAP, BAD_HOST);
    failure_case (BOOTSTRAP, BAD_PORT);
    failure_case (BOOTSTRAP, NULL);
    }
  return unhandled ();
}

HANDLE ("FileControl", FILE_CONTROL)
{
  switch (error)
    {
    success_case (FILE_CONTROL);
    failure_case (FILE_CONTROL, ALREADY_PAUSED);
    failure_case (FILE_CONTROL, DENIED);
    failure_case (FILE_CONTROL, FRIEND_NOT_CONNECTED);
    failure_case (FILE_CONTROL, FRIEND_NOT_FOUND);
    failure_case (FILE_CONTROL, NOT_FOUND);
    failure_case (FILE_CONTROL, NOT_PAUSED);
    failure_case (FILE_CONTROL, SENDQ);
    }
  return unhandled ();
}

HANDLE ("FileGet", FILE_GET)
{
  switch (error)
    {
    success_case (FILE_GET);
    failure_case (FILE_GET, FRIEND_NOT_FOUND);
    failure_case (FILE_GET, NOT_FOUND);
    failure_case (FILE_GET, NULL);
    }
  return unhandled ();
}

HANDLE ("FileSeek", FILE_SEEK)
{
  switch (error)
    {
    success_case (FILE_SEEK);
    failure_case (FILE_SEEK, DENIED);
    failure_case (FILE_SEEK, FRIEND_NOT_CONNECTED);
    failure_case (FILE_SEEK, FRIEND_NOT_FOUND);
    failure_case (FILE_SEEK, INVALID_POSITION);
    failure_case (FILE_SEEK, NOT_FOUND);
    failure_case (FILE_SEEK, SENDQ);
    }
  return unhandled ();
}

HANDLE ("FileSendChunk", FILE_SEND_CHUNK)
{
  switch (error)
    {
    success_case (FILE_SEND_CHUNK);
    failure_case (FILE_SEND_CHUNK, FRIEND_NOT_CONNECTED);
    failure_case (FILE_SEND_CHUNK, FRIEND_NOT_FOUND);
    failure_case (FILE_SEND_CHUNK, INVALID_LENGTH);
    failure_case (FILE_SEND_CHUNK, NOT_FOUND);
    failure_case (FILE_SEND_CHUNK, NOT_TRANSFERRING);
    failure_case (FILE_SEND_CHUNK, NULL);
    failure_case (FILE_SEND_CHUNK, SENDQ);
    failure_case (FILE_SEND_CHUNK, WRONG_POSITION);
    }
  return unhandled ();
}

HANDLE ("FileSend", FILE_SEND)
{
  switch (error)
    {
    success_case (FILE_SEND);
    failure_case (FILE_SEND, FRIEND_NOT_CONNECTED);
    failure_case (FILE_SEND, FRIEND_NOT_FOUND);
    failure_case (FILE_SEND, NAME_TOO_LONG);
    failure_case (FILE_SEND, NULL);
    failure_case (FILE_SEND, TOO_MANY);
    }
  return unhandled ();
}

HANDLE ("FriendAdd", FRIEND_ADD)
{
  switch (error)
    {
    success_case (FRIEND_ADD);
    failure_case (FRIEND_ADD, ALREADY_SENT);
    failure_case (FRIEND_ADD, BAD_CHECKSUM);
    failure_case (FRIEND_ADD, MALLOC);
    failure_case (FRIEND_ADD, NO_MESSAGE);
    failure_case (FRIEND_ADD, NULL);
    failure_case (FRIEND_ADD, OWN_KEY);
    failure_case (FRIEND_ADD, SET_NEW_NOSPAM);
    failure_case (FRIEND_ADD, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("FriendByPublicKey", FRIEND_BY_PUBLIC_KEY)
{
  switch (error)
    {
    success_case (FRIEND_BY_PUBLIC_KEY);
    failure_case (FRIEND_BY_PUBLIC_KEY, NOT_FOUND);
    failure_case (FRIEND_BY_PUBLIC_KEY, NULL);
    }
  return unhandled ();
}

HANDLE ("FriendCustomPacket", FRIEND_CUSTOM_PACKET)
{
  switch (error)
    {
    success_case (FRIEND_CUSTOM_PACKET);
    failure_case (FRIEND_CUSTOM_PACKET, EMPTY);
    failure_case (FRIEND_CUSTOM_PACKET, FRIEND_NOT_CONNECTED);
    failure_case (FRIEND_CUSTOM_PACKET, FRIEND_NOT_FOUND);
    failure_case (FRIEND_CUSTOM_PACKET, INVALID);
    failure_case (FRIEND_CUSTOM_PACKET, NULL);
    failure_case (FRIEND_CUSTOM_PACKET, SENDQ);
    failure_case (FRIEND_CUSTOM_PACKET, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("FriendDelete", FRIEND_DELETE)
{
  switch (error)
    {
    success_case (FRIEND_DELETE);
    failure_case (FRIEND_DELETE, FRIEND_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("FriendGetPublicKey", FRIEND_GET_PUBLIC_KEY)
{
  switch (error)
    {
    success_case (FRIEND_GET_PUBLIC_KEY);
    failure_case (FRIEND_GET_PUBLIC_KEY, FRIEND_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("FriendSendMessage", FRIEND_SEND_MESSAGE)
{
  switch (error)
    {
    success_case (FRIEND_SEND_MESSAGE);
    failure_case (FRIEND_SEND_MESSAGE, EMPTY);
    failure_case (FRIEND_SEND_MESSAGE, FRIEND_NOT_CONNECTED);
    failure_case (FRIEND_SEND_MESSAGE, FRIEND_NOT_FOUND);
    failure_case (FRIEND_SEND_MESSAGE, NULL);
    failure_case (FRIEND_SEND_MESSAGE, SENDQ);
    failure_case (FRIEND_SEND_MESSAGE, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("GetPort", GET_PORT)
{
  switch (error)
    {
    success_case (GET_PORT);
    failure_case (GET_PORT, NOT_BOUND);
    }
  return unhandled ();
}

HANDLE ("New", NEW)
{
  switch (error)
    {
    success_case (NEW);
    failure_case (NEW, LOAD_BAD_FORMAT);
    failure_case (NEW, LOAD_ENCRYPTED);
    failure_case (NEW, MALLOC);
    failure_case (NEW, NULL);
    failure_case (NEW, PORT_ALLOC);
    failure_case (NEW, PROXY_BAD_HOST);
    failure_case (NEW, PROXY_BAD_PORT);
    failure_case (NEW, PROXY_BAD_TYPE);
    failure_case (NEW, PROXY_NOT_FOUND);
    }
  return unhandled ();
}

HANDLE ("SetInfo", SET_INFO)
{
  switch (error)
    {
    success_case (SET_INFO);
    failure_case (SET_INFO, NULL);
    failure_case (SET_INFO, TOO_LONG);
    }
  return unhandled ();
}

HANDLE ("SetTyping", SET_TYPING)
{
  switch (error)
    {
    success_case (SET_TYPING);
    failure_case (SET_TYPING, FRIEND_NOT_FOUND);
    }
  return unhandled ();
}
