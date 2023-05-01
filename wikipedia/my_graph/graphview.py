import networkx as nx
import matplotlib.pyplot as plt
import csv
from tqdm import tqdm

edges = []
# NSI COMMENTAIRE : ouvrir le fichier csv
with open("E:/Wiki-Scrap/wikipedia/brain/csv/data.csv", 'r') as file:
    csvreader = csv.reader(file)
    for row in csvreader:
        tmp = []
        for col in row:
            tmp.append(col)
        edges.append(tmp)

# NSI COMMENTAIRE : créer le graphique
G = nx.Graph()
for i in tqdm(range(len(edges))):
    G.add_node(edges[i][0])
for e in edges:
    for j in range(len(e)):
        if j != 0:
            G.add_edge(e[0], e[j])

nx.draw_networkx(G, with_labels=False)
plt.show()
