# Smart Steve X
> The voice-first operating system.

Smart Steve X is a Java framework that lets you easily create your own commands and give them as "recipes" to a LLM. You can choose your model and provider, and all commands that you create are automatically sent/parsed on every prompt. 

**Build your own JARVIS!!!!**
---
Requirements:
- Java 21+
- Postgres (pgvector extension recommended)
- Microphone
  
**Quick Start**

### 1. Install
Add Smart Steve to your project.

**Maven:**
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
	    <groupId>com.github.Daviiipkp</groupId>
	    <artifactId>Smart-Steve-X</artifactId>
	    <version>v1.0.0</version>
	</dependency>
  </dependencies>
```



### 2. Configure
On your src/main/resources/application (.yml or .properties), configure these values:
```
spring.datasource.url
spring.datasource.username 
spring.datasource.password
spring.datasource.driver-class-name
```
And if you need any other specification, feel free to configure as well.
Example:
```
spring:
    application:
        name: your-awesome-app

    datasource:
        url: jdbc:postgresql://localhost:5432/postgres
        username: postgres
        password: password
        driver-class-name: org.postgresql.Driver

    jpa:
        database-platform: org.hibernate.dialect.PostgreSQLDialect
        hibernate:
            ddl-auto: update
        show-sql: true
```


### **3. Create your first Command**
As of now we have 5 primitive types of commands:
- InstantCommand, that is only executed once and ends;
- ParallelCommand, which has it's own Thread and doesn't wait for other commands;
- TriggeredCommand, which has a "checkTrigger" method that you MUST override, and once this method returns true, the execute() method will be called;
- QueuedCommand, which is command that you will most likely use the most. He goes to the queue (InstantCommand also does) which means he'll wait for other commands before start() and execute(). Also, he will only stop be executed once you call finish();
- CommandStack, which is a group of commands that are executed sequentially in a thread of their own.

And two types of Annotations:
- CommandDescription, which should be used to describe the command to the LLM (is also a way of the system knowing that your command is registered. Commands without this annotation will be ignored)
- Describe, which should only be used in fields **OF PRIMITIVE TYPE** that you want the LLM to fill (like how much time to wait for something)
To create your command, it must extend one of those classes.
Example:
```
@CommandDescription("Use this whenever the user is feeling down or sad.")
public class ComfortCommand extends QueuedCommand {
    
    @Describe(description = "A scale of 1-10 of how sad the user sounds")
    private int sadnessLevel;
    
    @Override
    public void start() {
        super.start();
        System.out.println("Initiating comfort protocol. Level: " + sadnessLevel);
    }

    @Override
    public void execute(long delta) {
        super.execute(delta);
        // Logic
        
        // When done, you MUST call finish() to free the queue
        if (isComfortComplete()) {
            finish(); 
        }
    }
}
```



### **4. Configure and start your program**
There's a class called Configuration which you really should explore. Any field that is not set in Configuration class will prevent the program to start.
Example configuration (with all the fields):
```
        public static void configure() {
	        // LLM
	        Configuration.LLM_PROVIDER = "https://api.groq.com/openai/v1/chat/completions";
	        Configuration.LLM_API_KEY = Secret.LLM_API_KEY;
	        Configuration.LLM_MODEL_NAME = "openai/gpt-oss-120b";
	
	        // Embeddings
	        Configuration.EMBEDDING_URL = "https://ai.hackclub.com/proxy";
	        Configuration.EMBEDDING_API_KEY = Secret.EMBEDDING_API_KEY;
	        Configuration.EMBEDDING_MODEL = "openai/text-embedding-3-small";
	
	        // Search
	        Configuration.SEARCH_API_KEY = Secret.SEARCH_API_KEY;
	        Configuration.SEARCH_PROVIDER = "https://search.hackclub.com/res/v1/web/search";
	        Configuration.PROTOCOL_SEARCH_NUMBER = 5;
	
	        // Package path
	        Configuration.USER_COMMAND_PACKAGE = "com.daviipkp.steveproductivitypack.implementations.commands";
	
	        // Booleans
	        Configuration.DO_WARM_UP = true;
	        Configuration.USE_VOICE_START_WORD = true;
	        Configuration.USE_VOICE_END_WORD = true;
	        Configuration.VOICE_TYPING_FEATURE = false;
	        Configuration.CLEAR_MEMO_ON_STARTUP = false;
	        Configuration.USE_DEFAULT_COMMANDS = true;
	
	        // Debug
	        Configuration.SHOW_VOICE_TEXT_DEBUG = false;
	        Configuration.MEMORY_DEBUG = false;
	        Configuration.USER_PROMPT_DEBUG = false;
	        Configuration.STEVE_RESPONSE_DEBUG = true;
	        Configuration.DATABASE_SAVING_DEBUG = false;
	        Configuration.FINAL_PROMPT_DEBUG = false;
	        Configuration.PROMPT_LATENCY_DEBUG = false;
	        Configuration.PROMPT_COMPONENTS_CONTENT_EMPTY_DEBUG = false;
	
	        // Strings
	        Configuration.VOICE_START_WORD = "steve";
	        Configuration.VOICE_END_WORD = "over";
	        Configuration.VOICE_TYPING_STOP_STRING = "terminate";
	        Configuration.FIRST_BOOT_INSTRUCTIONS = "System initialized.";
	        Configuration.ALARM_PATH = System.getProperty("user.dir") + java.io.File.separator + "alarm.mp3";
	    }
```

Then you can simply
```
  public static void main(String[] args) {
        configure();

        SmartsteveApplication.main(args);
    }
```















