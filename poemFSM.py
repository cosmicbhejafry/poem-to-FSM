import string
# hyphen separated words split
# fullstops? commas? dashes?
# apostrophes lmaooooo

def return_str_tup(input_str):
  input_str = input_str.translate(str.maketrans('', '', string.punctuation))
  input_str = input_str.strip()
  input_str = input_str.lower()
  return input_str.split(" ")

#read poem from txt file, convert to state-transition table 5
dict_states = {} #curr state -> [(in,out)]
all_nodes = []

with open("./temp.txt",'r') as fl:
  # print(xyz)
  line_inx = 0
  sent_transition_st = ""

  prefx = "line"
  for line in fl:
    lst = return_str_tup(line)
    all_nodes.extend(lst)
    # print(len(lst))

    if(len(lst)==1):
      print(len(lst[0]))
      # s = lst[0]
      if(len(lst[0])==0):
        prefx = "stanza"
        continue
    
    if(line_inx!=0):
      if sent_transition_st in dict_states.keys():
        dict_states[sent_transition_st].add("{}^".format(prefx)+ lst[0])
      else:
        dict_states[sent_transition_st] = set(["{}^".format(prefx)+ lst[0]])
      if(prefx=="stanza"):
        print("here?")
        prefx = "line"    


    for word_inx in range(0,len(lst)-1):
      curr_st = lst[word_inx]
      next_st = lst[word_inx+1]
      inpt = "para"
      if(line_inx==0 and word_inx==0):
        inpt = "start"
      
      fsm_str = inpt+"^"+next_st
      
      if curr_st in dict_states.keys():
        dict_states[curr_st].add(fsm_str)
      else:
        dict_states[curr_st]=set([fsm_str])
      
      sent_transition_st = next_st
    # print(next_st)
    line_inx +=1

all_nodes = set(all_nodes)

print("digraph G{")
print("\t" + "size=\"{}{}\"".format(size[0],size[1]))
print("\t" + "rankdir=\"LR\"")

for k in dict_states.keys():
  for finl in dict_states[k]:
      finl1 = finl.split('^')
      # print(finl1)
      # print(k)
      G.add_weighted_edges_from([(k,finl1[-1],finl1[0])])
      start_node_label = k
      end_node_label = finl1[-1]
      label = finl1[0]
      if(label=="para"):
        # print(start_node_label + "->" + end_node_label + " [label=\"" + label + "\"];")
        print("\t" + start_node_label + "->" + end_node_label + "[color=blue];")
      if(label=="line"):
        print("\t" + start_node_label + "->" + end_node_label + " [label=\"" + label + "\",color=coral];")
      if(label=="stanza"):
        print("\t"  + start_node_label + "->" + end_node_label + " [label=\"" + label + "\",style=dotted];")

      if(label=="start"):
        print("\t"  + start_node_label + "->" + end_node_label + " [label=\"" + label + "\",color=violet];")

print("}")
