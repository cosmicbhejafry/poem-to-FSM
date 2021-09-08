 ## poem-to-FSM: How does one characterize the flow of poetry? [Experiments ongoing]
I try to examine that by representing a poem like a state machine, where the transitions are 'breaks' - the end of a word, line, or stanza; and the states are words. 

_You can now download am **unstable** java executable UI for Windows: [here for 64-bit](https://github.com/cosmicbhejafry/poem-to-FSM/tree/main/processing_JavaExecutable/invoker/application.windows64) or [here for 32-bit](https://github.com/cosmicbhejafry/poem-to-FSM/tree/main/processing_JavaExecutable/invoker/application.windows32). Make sure you read the [instructions](https://github.com/cosmicbhejafry/poem-to-FSM/blob/main/processing_JavaExecutable/invoker/instruct.md) before using it._ It is built upon [@\zshiba's implementation](https://github.com/zshiba/visualization) of a Force Directed Graph in Processing.

If you prefer, the poemFSM.py script basically 'generates' dot code which I recommend copy-pasting to [this](https://dreampuf.github.io/GraphvizOnline/#digraph%20G%20%7B%0A%0A%7D) site which enables you to visualize DOT code online. If it feels like too much setup, you can just run python code on an online compiler & interpreter like [Replit](https://replit.com/new/python3). 

**Please share any interesting results with me! :)**

Examples:

[you fit into me](https://www.poetryfoundation.org/poems/151653/you-fit-into-me) by Margaret Atwood

![graphviz](https://user-images.githubusercontent.com/47458458/123553482-faf08680-d798-11eb-81e1-4fdf0a37c653.png)

First 3 couplets from [Land](https://www.poetryfoundation.org/poetrymagazine/poems/41227/land) by Agha Shahid Ali

![graphviz (4)](https://user-images.githubusercontent.com/47458458/123553961-80753600-d79b-11eb-9f1d-2b2639b7473e.png)

