package client.testing;

import client.testing.model.ResponseMessage.MessageType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class GsonFactory
{
  private static final GsonFactory DEFAULT_FACTORY = new GsonFactory();
  private static final Gson DEFAULT_GSON = new GsonBuilder().create();
  private static final Gson PROTOCOL_GSON = new GsonBuilder().setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).toPattern()).registerTypeAdapter(ai.api.model.ResponseMessage.class, new ResponseItemAdapter(null)).registerTypeAdapter(ai.api.model.ResponseMessage.ResponseSpeech.class, new ResponseSpeechDeserializer(null)).create();
  
  public GsonFactory() {}
  
  public static GsonFactory getDefaultFactory()
  {
    return DEFAULT_FACTORY;
  }
  
  public Gson getGson()
  {
    return PROTOCOL_GSON;
  }
  
  class ResponseItemAdapter
    implements JsonDeserializer<ai.api.model.ResponseMessage>, JsonSerializer<ai.api.model.ResponseMessage>
  {
    private ResponseItemAdapter() {}
    
    public client.testing.model.ResponseMessage deserialize(JsonElement paramJsonElement, Type paramType, JsonDeserializationContext paramJsonDeserializationContext)
      throws JsonParseException
    {
      int j = paramJsonElement.getAsJsonObject().get("type").getAsInt();
      paramType = ResponseMessage.MessageType.values();
      int k = paramType.length;
      int i = 0;
      while (i < k)
      {
        Object localObject = paramType[i];
        if (localObject.getCode() == j) {
          return (client.testing.model.ResponseMessage)paramJsonDeserializationContext.deserialize(paramJsonElement, localObject.getType());
        }
        i += 1;
      }
      throw ((Throwable)new JsonParseException(String.format("Unexpected message type value: %d", new Object[] { Integer.valueOf(j) })));
    }
    
    public JsonElement serialize(client.testing.model.ResponseMessage paramResponseMessage, Type paramType, JsonSerializationContext paramJsonSerializationContext)
    {
      return paramJsonSerializationContext.serialize(paramResponseMessage, paramResponseMessage.getClass());
    }
  }
  
  class ResponseSpeechDeserializer
    implements JsonDeserializer<ai.api.model.ResponseMessage>
  {
    private ResponseSpeechDeserializer() {}
    
    public client.testing.model.ResponseMessage.ResponseSpeech deserialize(JsonElement paramJsonElement, Type paramType, JsonDeserializationContext paramJsonDeserializationContext)
      throws JsonParseException
    {
      if (paramJsonElement.isJsonObject())
      {
        paramJsonDeserializationContext = (JsonObject)paramJsonElement;
        if (paramJsonDeserializationContext.get("speech").isJsonPrimitive())
        {
          JsonArray localJsonArray = new JsonArray();
          localJsonArray.add(paramJsonDeserializationContext.get("speech"));
          paramJsonDeserializationContext.add("speech", localJsonArray);
        }
      }
      return (client.testing.model.ResponseMessage.ResponseSpeech)GsonFactory.DEFAULT_GSON.fromJson(paramJsonElement, paramType);
    }
  }
}
