#! /usr/bin/env python

import argparse

def main(args):
    prev_nobjects = 0
    ntransitions = 0
    nadsorptions = 0
    ndesorptions = 0
    with open(args.input) as f:
        for line in f:
            ntransitions += 1
            entry = line.strip().split('\t')
            nobjects = int(entry[1])
            if nobjects > prev_nobjects:
                nadsorptions += 1
            elif nobjects < prev_nobjects:
                ndesorptions += 1
            prev_nobjects = nobjects
                
    print "%d adsorptions, %d desorptions out of %d total transitions" % (nadsorptions, ndesorptions, ntransitions)
    
def opts():
    parser = argparse.ArgumentParser()
    parser.add_argument('input', help='Input file with nobjects vs. time')
    return parser

if __name__ == '__main__':
    main(opts().parse_args())