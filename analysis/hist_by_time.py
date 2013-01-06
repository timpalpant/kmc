#! /usr/bin/env python

import argparse, numpy

def main(args):
    (times,counts) = numpy.loadtxt(args.input, usecols=(args.timecol,args.valuecol),
                                   dtype=(float,int), unpack=True)
    counts = numpy.array(counts, dtype=int)
    maxcounts = max(counts)
    hist = [0.] * (maxcounts+1)
    dt = times[1:] - times[:-1]
    for i,n in enumerate(counts[:-1]):
        hist[n] += dt[i]
    for i,v in enumerate(hist):
        print "%d\t%g" % (i,v/times[-1])
    
def opts():
    parser = argparse.ArgumentParser()
    parser.add_argument('input', help='Input file with times and counts')
    parser.add_argument('--timecol', type=int, default=0, help='Column with time values')
    parser.add_argument('--valuecol', type=int, default=1, help='Column with counts to histogram')
    return parser

if __name__ == '__main__':
    main(opts().parse_args())