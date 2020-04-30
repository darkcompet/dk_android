# About scopedTopic

When you wanna pass a bundle of data from a View (Activity, Fragment...) to other Views.
That is, some Views interest to listen to a bundle, and after all of them got destroyed,
the bundle should be deleted.

To solve that problem, we introduce a sub-module called `scopedTopic` to make developer
easily to handle it.

## Theory

We need 3 aliases: `Host, Topic, Client`.

- Create a Host (owned by host owner) which contains list of Topic, and list of pair Topic-Client.
- When a Client (owned by client owner) connect to the host, we register that client and make a link between Topic-Client.
- When a Client disconnect, we remove all links between Topic-Client.
- If a Topic is inactive (no Client register it), then remove that topic from Host.

## Implementation

Suppose we have 1 Activity X and 2 Fragments A, and B.

```java
   class X extends FragmentActivity {
      void onCreate() {
         // just get model, not register
         boolean register = false;
         Hacker hacker = getOwnTopic(Hacker.class, register);
         
         hacker.setName("X");
         
         addFragment(A.class);
      }
      
      void onBackPressed() {
         if (isFragmentPresent(B.class)) {
            popFragment(B.class);
         }
         else if (isFragmentPresent(A.class)) {
            popFragment(A.class);
         }
         else {
            // When user back 2 times to finish fragment B -> A
            // then activity X will be display here.
            // At this time, Hacker topic will be removed from ViewModelStore of X.  
            Hacker hacker = getOwnTopic(Hacker.class, false);
            
            // print null
            Log.d("X~ ", hacker.getName());
         }
      }
   }

   class A extends Fragment {
      void onCreate() {
         // get model and also register
         boolean register = true;
         Hacker hacker = getHostTopic(Hacker.class, register);
         
         // print X
         Log.d("A~ ", hacker.getName());
         
         hacker.setName("A");
         
         addFragment(B.class);
      }
      
      void finish() {
      }
   }
   
   class B extends Fragment {
      void onCreate() {
         // get model and also register
         boolean register = true;
         Hacker hacker = getHostTopic(Hacker.class, register);
         
         // print A
         Log.d("B~ ", hacker.getName());
         
         // set back name
         hacker.setName("B");
      }
      
      void finish() {
      }
   }
```
