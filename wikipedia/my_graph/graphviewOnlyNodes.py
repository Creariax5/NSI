import networkx as nx
import matplotlib.pyplot as plt
import csv
from tqdm import tqdm

edges = []
with open("C:/florian/dev/NSI/NSI/wikipedia/brain/csv/data.csv", 'r') as file:
    csvreader = csv.reader(file)
    for row in csvreader:
        tmp = []
        for col in row:
            tmp.append(col)
        edges.append(tmp)

G = nx.Graph()
my_list = []
for i in tqdm(range(len(edges))):
    G.add_node(edges[i][0])
    my_list.append(edges[i][0])
for e in edges:
    for j in range(len(e)):
        # NSI COMMENTAIRE : afficher que les liens dont la page a été scrap
        if e[j] in my_list and j != 0:
            G.add_edge(e[0], e[j])

nx.draw_networkx(G, with_labels=True, edge_color="#FFF000")
plt.show()
