#! /usr/bin/env python3

import argparse
import json
import sys


def get_parser():
    parser = argparse.ArgumentParser(description='Parse json to text file.')
    parser.add_argument('--url')
    parser.add_argument('--input', default="None")
    parser.add_argument('--output', default="sys.stdout")
    parser.add_argument(
        '--type',
        required=True,
        default="task",
        help="Must be one of [\"task\", \"resource\", \"scheduling\"]")
    return parser


def get_jobs_name(jobs):
    job_names = list()
    for job_id in jobs.keys():
        job_names.append("Job-%s" % job_id)
    return job_names.__str__()


def get_flows_name(flows):
    flow_names = list()
    for flow in flows.values():
        flow_names.append("Flow-%d" % flow["flow-id"])
    return flow_names.__str__()


def get_flow_description(flow, bandwidth=None):
    if not bandwidth:
        description = "The Flow-%d is from %s to %s. It has completed its path query and get its path: %s.\n" % (
            flow["flow-id"], flow["src-ip"], flow["dst-ip"],
            flow["path"].__str__())
    else:
        avail_bw = flow["avail-bw"] / (1000 * 1000)
        description = "The Flow-%d is from %s to %s. It is scheduled to %d Gbps.\n" % (
            flow["flow-id"], flow["src-ip"], flow["dst-ip"], round(avail_bw))
    return description


def parse_task(input, output):
    output.write("The task %d has %d job(s): %s.\n" %
                 (input["task-id"], len(input["jobs"]),
                  get_jobs_name(input["jobs"])))
    for job in input["jobs"].values():
        output.write(
            "For Job-%d, it has %d flow(s): %s, and all of them complete their path queries.\n"
            % (job["job-id"], len(job["flows"]), get_flows_name(job["flows"])))
        for flow in job["flows"].values():
            output.write(get_flow_description(flow))
    output.write("\n")

def get_constraint(vector, limit):
    items = list()
    for item in vector:
        try:
            coefficient = item["coefficient"]
        except KeyError:
            coefficient = 1
        if coefficient == 0:
            continue
        coefficient = str(coefficient) if coefficient != 1 else ""
        flow_name = "Flow-" + item["flow-id"]
        items.append(coefficient + " " + flow_name)
    return " + ".join(items) + " <= " + str(
        ceil(limit["availbw"] / (1000 * 1000 ))) + " Gbps"


def get_constraints(ane_matrix, anes):
    description = ""
    for vector, limit in zip(ane_matrix, anes):
        constraint = get_constraint(vector, limit)
        description += ("    " + constraint + "\n")
    description
    return description += ("\n\n")


def parse_resource(input, output):
    for domain_name in input.keys():
        response = input[domain_name]["response"]
        output.write(
            "%s returns such constraints:\n%s" %
            (domain_name,
             get_constraints(response["ane-matrix"], response["anes"])))


def parse_scheduling(input, output):
    output.write("The task has %d job(s): %s\n" % (len(input),
                                                   get_jobs_name(input)))
    for job_id in input.keys():
        job = input[job_id]
        output.write("For Job-%s, it has %d flows: %s\n" %
                     (job_id, len(job), get_flows_name(job)))
        for flow in job.values():
            output.write(get_flow_description(flow, bandwidth=True))


def main():
    args = get_parser().parse_args()
    if args.input == "None":
        import requests
        input = requests.get(args.url).content
        input = json.loads(input)
    else:
        input = json.load(args.input)

    if args.output == "sys.stdout":
        output = sys.stdout
    else:
        output = open(args.output, 'w')

    if args.type == "task":
        parse_task(input, output)
    elif args.type == "resource":
        parse_resource(input, output)
    elif args.type == "scheduling":
        parse_scheduling(input, output)

    if output != sys.stdout:
        output.close()


if __name__ == '__main__':
    main()
