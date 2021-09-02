#!C:\Python27\python.exe
# https://paulmouzas.github.io/2015/04/19/cgi-http-server.html
import cgi, cgitb
# Create instance of FieldStorage
form = cgi.FieldStorage()
# Get data from fields
url = form.getvalue('poem_url')
print("Content-Type: text/html; charset=utf-8\n\n")
# print()
# import urllib.request
# import bs4 as bs
# import re
# hdr = {'User-Agent':'Mozilla/5.0'}
# req = urllib.request.Request(url,headers=hdr)
# sauce = urllib.request.urlopen(req).read()
# soup = bs.BeautifulSoup(sauce,'html.parser') #Beautiful Soup object
# soup.find_all('div', class_="o-poem")

# def pretty_text(text):
#     final = (((text).replace(u'\xa0', u' ')).replace(u'\r ',u'\n'))
#     return final

# poem = (pretty_text(soup.find_all('div', class_="o-poem")[0].text))
# title = soup.find_all('h1')[0].text
# poet = soup.find_all('a', href=re.compile('.*poets/.*'))[0].text

# with open("./poem.txt","w") as fl:
#     fl.write(title)
#     fl.write("By " + poet)
#     fl.write(poem)

# poem = poem.strip()

# import string

# def return_str_tup(input_str):
#   input_str = input_str.translate(str.maketrans('', '', string.punctuation))
#   input_str = input_str.strip()
#   input_str = input_str.lower()
#   return input_str.split(" ")

# dict_states = {} #curr state -> [(in,out)]
# all_nodes = []

# if(1==1):
#   # # print(xyz)
#   line_inx = 0
#   sent_transition_st = ""

#   prefx = "line"
#   for line in poem.split('\n'):
#     lst = return_str_tup(line)
#     all_nodes.extend(lst)
#     # # print(len(lst))

#     if(len(lst)==1):
#       # print(len(lst[0]))
#       # s = lst[0]
#       if(len(lst[0])==0):
#         prefx = "stanza"
#         continue
    
#     if(line_inx!=0):
#       if sent_transition_st in dict_states.keys():
#         dict_states[sent_transition_st].add("{}^".format(prefx)+ lst[0])
#       else:
#         dict_states[sent_transition_st] = set(["{}^".format(prefx)+ lst[0]])
#       if(prefx=="stanza"):
#         # print("here?")
#         prefx = "line"    


#     for word_inx in range(0,len(lst)-1):
#       curr_st = lst[word_inx]
#       next_st = lst[word_inx+1]
#       inpt = "para"
#       if(line_inx==0 and word_inx==0):
#         inpt = "start"
      
#       fsm_str = inpt+"^"+next_st
      
#       if curr_st in dict_states.keys():
#         dict_states[curr_st].add(fsm_str)
#       else:
#         dict_states[curr_st]=set([fsm_str])
      
#       sent_transition_st = next_st
#     # # print(next_st)
#     line_inx +=1

# all_nodes = set(all_nodes)

# import networkx as nx
# G = nx.MultiDiGraph()
# G.add_nodes_from(all_nodes)
# size = [20,20]
# # print("digraph G{")
# # print("\t" + "size=\"{}{}\"".format(size[0],size[1]))
# # print("\t" + "rankdir=\"LR\"")

# for k in dict_states.keys():
#   for finl in dict_states[k]:
#       finl1 = finl.split('^')
#       # # print(finl1)
#       # # print(k)
#       G.add_weighted_edges_from([(k,finl1[-1],finl1[0])])
#       start_node_label = k
#       end_node_label = finl1[-1]
#       label = finl1[0]


# from networkx.readwrite import json_graph
# graph_json = json_graph.node_link_data(G)
# # graph_json
# import json
# with open("./pcap_export.json","wt") as fl:
#   json.dump(graph_json,fl)

# print("Content-Type: text/html; charset=utf-8\n\n")