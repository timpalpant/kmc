#! /usr/bin/env python

import argparse, numpy

def main(args):
    p = numpy.loadtxt(args.input, usecols=[1])
    print sum(p[args.nucsize:])
    
def opts():
    parser = argparse.ArgumentParser()
    parser.add_argument('input', help='Input file with linker distribution')
    parser.add_argument('--nucsize', type=int, default=147, help='Nucleosome size (bp)')
    return parser

if __name__ == '__main__':
    main(opts().parse_args())