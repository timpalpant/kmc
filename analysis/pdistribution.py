#! /usr/bin/env python

import argparse

def main(args):
    counts = [0.]
    pos = None
    prev_time = None
    with open(args.input) as f:
        for line in f:
            entry = line.strip().split('\t')
            time = float(entry[0])
            if prev_time is not None:
                dt = time - prev_time
                for p in pos:
                    if p >= len(counts):
                        counts += [0.] * (p-len(counts)+1)
                    counts[p] += dt
            pos = map(int, entry[1].split(',')) if len(entry) > 1 else []
            prev_time = time
                
    with open(args.output, 'w') as out:
        for i,c in enumerate(counts):
            print >>out, "%d\t%g" % (i,c/time)
    
def opts():
    parser = argparse.ArgumentParser()
    parser.add_argument('input', help='Simulation input file')
    parser.add_argument('output', help='Output file with probability at each lattice position')
    return parser

if __name__ == '__main__':
    main(opts().parse_args())