#! /usr/bin/env python

import argparse

def main(args):
    with open(args.output, 'w') as out:
        with open(args.input) as f:
            for line in f:
                entry = line.strip().split('\t')
                time = entry[0]
                nobjects = len(entry[1].split(',')) if len(entry) > 1 else 0
                print >>out, '%s\t%d' % (time, nobjects)
    
def opts():
    parser = argparse.ArgumentParser()
    parser.add_argument('input', help='Simulation input file')
    parser.add_argument('output', help='Output file with # objects vs. time')
    return parser

if __name__ == '__main__':
    main(opts().parse_args())