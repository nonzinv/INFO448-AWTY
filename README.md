# INFO448-AWTY

__Are We There Yet? (Part 1)__:

Remember when you were a kid on a long car ride, and you kept asking your parents "Are we there yet?" and annoying the heck out of them? We want an application that does exactly that.

In this part, we will just pop Toast asking the question; in a future part, we will wire it up against SMS so that you can annoy the heck out of somebody else over text messages.

__Stories:__

As a user, when I start the application, it should present me with an activity containing an EditText for the message I want to send, an EditText for the phone number to which to send it (which we will not do anything with for this part), an EditText for how many minutes between each nag (no zeros, no negatives, must be an integer), and a Button labeled "Start".

As a user, when I am using the application's UI, "Start" should do nothing until all controls are filled out with legitimate values.

As a user, when I am using the application's UI, when "Start" is pressed, the Button's text should switch to "Stop", and pushing "Stop" should cause the service to stop sending messages.

As a user, when I "Start" the service, it should begin to "send messages" every N minutes, as given by the user in the EditText UI. For now, these should be Toast messages using the format: "(425) 555-1212: Are we there yet?".

As a user, when I switch away from this application to another one, the messages should continue to send every N minutes, and should continue to send until I navigate back to this application and push "Stop".

