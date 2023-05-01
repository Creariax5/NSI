import time
import requests
from bs4 import BeautifulSoup
import markdownify
from other.fonc import *

# NSI COMMENTAIRE : demande à l'utilisateur un nom d'article wiki.
inp = input("Donnez un nom d'article wikipedia: ")

# NSI COMMENTAIRE : wiki. url
url = 'https://fr.wikipedia.org'
# NSI COMMENTAIRE : reconstitute l'adresse web et la met dans la liste de lins vide
links = ['/wiki/' + inp]
# NSI COMMENTAIRE : emplacement du fichier
file_url = "brain/"

i = 0

# NSI COMMENTAIRE : boucle qui execute le code temps qu'il y a des liens dans 'links'
while i <= len(links):
    # NSI COMMENTAIRE : créé une liste vide
    case = []
    print("begin request: " + links[i])
    # NSI COMMENTAIRE : fait une requete et stocke tout le code de la page web donc l'adresse est la concatenation de url et links à l'indice de i
    result = requests.get(url + links[i])
    # NSI COMMENTAIRE : 'if' teste si il n'y a pas eu d'erreur
    if result.ok:
        # NSI COMMENTAIRE : seul le html est récupéré dans 'html
        html = result.text
        # NSI COMMENTAIRE : un object soup est créé du html avec le format correspondant ici 'lxml'
        soup = BeautifulSoup(html, 'lxml')

        # NSI COMMENTAIRE : récupère le titre de la page dans 'title'
        title = soup.find("title").text
        # NSI COMMENTAIRE : la fonction 'transform_title' est une fonction que j'ai codée dans le fichier fonc.py,
        # elle permet de mettre le titre de la page au même format que les liens
        title = transform_title(title)

        # NSI COMMENTAIRE : try permet de ne pas planter le programme si une page est inaccessible
        try:
            # NSI COMMENTAIRE : je mets dans 'content' le html qui m'intéresse en récupérant toutes
            # les classes nommées 'mw-parser-output' sous forme de liste
            content = soup.findAll(class_="mw-parser-output")
            # NSI COMMENTAIRE : l'avant-dernière div dans 'content'
            content = content[len(content)-1]
            # NSI COMMENTAIRE : on recup tous les liens de 'content' et on les ajoute à 'links'
            scraped_links = content.findAll("a")
            for a in scraped_links:
                try:
                    link = a['href']
                    link_first_five = ""
                    for j in range(5):
                        link_first_five += link[j]
                    if link_first_five == '/wiki':
                        if not contain_point(link):
                            case.append(link)
                            links.append(link)
                except:
                    pass
            content = str(content)
            # NSI COMMENTAIRE : converti .html en .md
            md = markdownify.markdownify(content, heading_style="ATX")

            # NSI COMMENTAIRE : met tous les lins recup sur la page au bon format et met au format csv
            tmp_case = []
            for c in case:
                c = remove_wiki(c)
                tmp_case.append(c)

            csv = create_csv(title, tmp_case)

            # NSI COMMENTAIRE : enregistrer les fichiers
            file_name = title
            '''with open(file_url + 'html/' + file_name + ".html", "w", encoding="utf-8") as f:
                f.write(content)
            with open(file_url + 'md/' + "wiki/" + file_name + ".md", "w", encoding="utf-8") as f:
                f.write(md)'''
            f = open(file_url + 'csv/' + "data" + ".csv", "a", encoding="utf-8")
            f.write(csv + "\n")
            f.close()
            print(title + " saved")
        except Exception as e:
            print(e)
        i += 1
    time.sleep(0)
