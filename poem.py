#!/usr/bin/env python
# -*- coding: utf-8 -*-

import urllib, ftfy, re, string, random, unicodedata
from bs4 import BeautifulSoup

PARAM = random.choice(string.letters)
URL = '''https://www.poetryinvoice.com/poems/random-poem?''' + PARAM + '=' + PARAM
HEAD = 'Content-type: text/html\n\n'

def fix_text(s):
    return ftfy.fix_text(s.text)

def main():
    html = urllib.urlopen(URL).read()
    soup = BeautifulSoup(html, features='html5lib')
    title = soup.findAll('h1')[1]
    author = soup.find('h4')
    poem = ('<b>' + fix_text(title) + ' by:' + fix_text(author) + '</b>')
    for p in soup.findAll('p'):
        poem += '<p>'
        poem += ftfy.fix_text(p.text)
        poem += '</p>'
        poem = re.sub(ur'\u2014', '&#8212;', poem)
        poem = unicodedata.normalize('NFKD', poem)
    return poem

if __name__ == '__main__':
    print(HEAD + main().encode('utf-8'))
